import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { FinalizationEventData } from '@/features/current-session/Stopwatch/StopwatchRoot';
import _ from 'lodash';
import { StateCreator, StoreApi } from 'zustand';

type BroadcastType = 'events' | 'projectInfo' | 'reset' | 'activeDialogTabId';

type BroadcastOptions = {
  broadcastChange?: boolean;
  broadcastType?: BroadcastType;
};

type BroadcastMessage = {
  type: BroadcastType;
  data: Partial<FinalizationEventData & { activeDialogTabId: string | null }>;
};

export const createBroadcastMiddleware =
  (channelName: string) =>
  <
    T extends {
      events: StopwatchSessionEvent[];
      projectName: string;
      hourlyRate: number;
      activeDialogTabId: string | null;
    },
  >(
    f: StateCreator<T>,
  ): StateCreator<T> =>
  (set, get, api) => {
    const channel = new BroadcastChannel(channelName);

    /**
     * Sets the new state on the local instance
     * and broadcasts the change
     * to the other channels.
     */
    const setWithBroadcast: StoreApi<T>['setState'] = (
      updates,
      replace,
      options: BroadcastOptions = {
        broadcastChange: false,
      },
    ) => {
      // Update state
      set(
        (state) => ({
          ...((typeof updates === 'function'
            ? updates(state)
            : updates) as object),
        }),
        replace,
      );

      if (options?.broadcastChange && options?.broadcastType) {
        const { events, projectName, hourlyRate, activeDialogTabId } = get();

        // Create Broadcast Message
        const message: BroadcastMessage = {
          type: options.broadcastType,
          data: {},
        };

        // Populate data depending on broadcastType
        switch (options.broadcastType) {
          case 'events':
            message.data.events = events;
            break;
          case 'projectInfo':
            message.data.projectName = projectName;
            message.data.hourlyRate = hourlyRate;
            break;
          case 'reset':
            // No additional data needed for reset
            break;
          case 'activeDialogTabId':
            message.data.activeDialogTabId = activeDialogTabId;
            break;
        }

        channel.postMessage(message);
      }
    };

    channel.onmessage = (event: MessageEvent<BroadcastMessage>) => {
      const { type: broadcastType, data: remoteData } = event.data;
      const localData = get();

      switch (broadcastType) {
        case 'events':
          if (!_.isEqual(remoteData.events, localData.events)) {
            set({ ...localData, events: remoteData.events });
          }
          break;
        case 'projectInfo':
          if (
            !_.isEqual(remoteData.projectName, localData.projectName) ||
            !_.isEqual(remoteData.hourlyRate, localData.hourlyRate)
          ) {
            set({
              ...localData,
              projectName: remoteData.projectName ?? localData.projectName,
              hourlyRate: remoteData.hourlyRate ?? localData.hourlyRate,
            });
          }
          break;
        case 'reset':
          set(f(setWithBroadcast, get, api));
          break;
        case 'activeDialogTabId':
          if (
            !_.isEqual(
              remoteData.activeDialogTabId,
              localData.activeDialogTabId,
            )
          ) {
            set({
              ...localData,
              activeDialogTabId: remoteData.activeDialogTabId,
            });
          }
          break;
      }
    };

    return f(setWithBroadcast, get, api);
  };

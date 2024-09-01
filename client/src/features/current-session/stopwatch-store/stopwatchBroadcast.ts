import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import _ from 'lodash';
import { StateCreator, StoreApi } from 'zustand';
import { FinalizationEventData } from '@/features/current-session/Stopwatch/StopwatchRoot';

type BroadcastType = 'events' | 'projectInfo';

type BroadcastOptions = {
  broadcastChange?: boolean;
  broadcastType?: BroadcastType;
};

type BroadcastMessage = {
  type: BroadcastType;
  data: Partial<FinalizationEventData>;
};

export const createBroadcastMiddleware =
  (channelName: string) =>
  <
    T extends {
      events: StopwatchSessionEvent[];
      projectName: string;
      hourlyRate: number;
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
        const { events, projectName, hourlyRate } = get();

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
      }
    };

    return f(setWithBroadcast, get, api);
  };

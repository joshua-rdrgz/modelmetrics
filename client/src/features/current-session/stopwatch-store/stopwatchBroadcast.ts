import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import _ from 'lodash';
import { StateCreator, StoreApi } from 'zustand';

type BroadcastOptions = { broadcastChange?: boolean };

type BroadcastMessage = {
  events: StopwatchSessionEvent[];
  elapsedTime: number;
  isStopwatchRunning: boolean;
  projectName: string;
  hourlyRate: number;
};

export const createBroadcastMiddleware =
  (channelName: string) =>
  <T extends BroadcastMessage>(f: StateCreator<T>): StateCreator<T> =>
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

      // Broadcast if necessary
      if (options?.broadcastChange) {
        const {
          events,
          elapsedTime,
          isStopwatchRunning,
          projectName,
          hourlyRate,
        } = get();

        console.log('sending....');
        channel.postMessage({
          events,
          elapsedTime,
          isStopwatchRunning,
          projectName,
          hourlyRate,
        });
      }
    };

    /**
     * Receives state change from another channel
     * and syncs its own state with the change.
     */
    channel.onmessage = (event: MessageEvent<BroadcastMessage>) => {
      const remoteData = event.data;
      const {
        events,
        elapsedTime,
        isStopwatchRunning,
        projectName,
        hourlyRate,
      } = get();

      const localData = {
        events,
        elapsedTime,
        isStopwatchRunning,
        projectName,
        hourlyRate,
      };

      const shouldSync =
        !_.isEqual(remoteData.events, localData.events) ||
        !_.isEqual(remoteData.projectName, localData.projectName) ||
        !_.isEqual(remoteData.hourlyRate, localData.hourlyRate);

      if (shouldSync) {
        console.log('syncing....');
        set({
          ...get(),
          ...remoteData,
        });
      }
    };

    return f(setWithBroadcast, get, api);
  };

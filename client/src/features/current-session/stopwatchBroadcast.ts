import { StopwatchSessionEvent } from '@/features/current-session/stopwatchSessionStore';
import { StateCreator, StoreApi } from 'zustand';
import _ from 'lodash';

type BroadcastOptions = { broadcastChange?: boolean };

type BroadcastMessage = {
  events: StopwatchSessionEvent[];
  elapsedTime: number;
  isRunning: boolean;
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
        const { events, elapsedTime, isRunning } = get();
        console.log('sending....');
        channel.postMessage({
          events,
          elapsedTime,
          isRunning,
        });
      }
    };

    /**
     * Receives state change from another channel
     * and syncs its own state with the change.
     */
    channel.onmessage = (event: MessageEvent<BroadcastMessage>) => {
      const remoteData = event.data;
      const { events, elapsedTime, isRunning } = get();

      const localData = {
        events,
        elapsedTime,
        isRunning,
      };

      if (!_.isEqual(remoteData.events, localData.events)) {
        console.log('syncing....');
        set({
          ...get(),
          ...remoteData,
        });
      }
    };

    return f(setWithBroadcast, get, api);
  };

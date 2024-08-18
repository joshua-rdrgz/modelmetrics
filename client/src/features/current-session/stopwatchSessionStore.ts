import { createBroadcastMiddleware } from '@/features/current-session/stopwatchBroadcast';
import { create, StateCreator } from 'zustand';
import { createJSONStorage, devtools, persist } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';

export type StopwatchEventType =
  | 'start'
  | 'break'
  | 'resume'
  | 'taskComplete'
  | 'finish';

export interface StopwatchSessionEvent {
  type: StopwatchEventType;
  timestamp: number;
}

interface StopwatchSessionState {
  events: StopwatchSessionEvent[];
  elapsedTime: number;
  isRunning: boolean;
  addEvent: (type: StopwatchEventType) => Promise<void>;
  setIsRunning: (isRunning: boolean) => void;
  setElapsedTime: (time: number) => void;
  resetSession: () => void;
}

export const useStopwatchSessionStore = create<StopwatchSessionState>()(
  devtools(
    persist(
      createBroadcastMiddleware('stopwatch-channel')(
        immer((set) => ({
          events: [],
          elapsedTime: 0,
          isRunning: false,
          /**
           * Adds event to the events array.
           * Will be broadcast to other
           * browser tabs.
           */
          addEvent: async (type) =>
            set(
              (state) => {
                state.events.push({ type, timestamp: Date.now() });
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              {
                broadcastChange: true,
              },
            ),
          setIsRunning: (isRunning) => set({ isRunning }),
          setElapsedTime: (time) => set({ elapsedTime: time }),
          resetSession: () =>
            set({ events: [], isRunning: false, elapsedTime: 0 }),
        })) as StateCreator<StopwatchSessionState, [['zustand/immer', never]]>,
      ),
      {
        name: 'stopwatch-session-storage',
        storage: createJSONStorage(() => localStorage),
      },
    ),
  ),
);

import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { devtools, persist, createJSONStorage } from 'zustand/middleware';
import * as Comlink from 'comlink';

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

type StopwatchWorker = {
  start: (initialElapsedTime: number) => void;
  stop: () => void;
  getElapsedTime: () => Promise<number>;
};

const worker = new Worker(new URL('./stopwatch.worker.ts', import.meta.url), {
  type: 'module',
});
const stopwatchWorker = Comlink.wrap<StopwatchWorker>(worker);

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
      immer((set) => ({
        events: [],
        elapsedTime: 0,
        isRunning: false,
        addEvent: async (type) => {
          set((state) => {
            state.events.push({ type, timestamp: Date.now() });
          });
        },
        setIsRunning: (isRunning) => set({ isRunning }),
        setElapsedTime: (time) => set({ elapsedTime: time }),
        resetSession: () =>
          set({ events: [], isRunning: false, elapsedTime: 0 }),
      })),
      {
        name: 'stopwatch-session-storage',
        storage: createJSONStorage(() => localStorage),
      },
    ),
  ),
);

// Export worker for global usage
export { stopwatchWorker };

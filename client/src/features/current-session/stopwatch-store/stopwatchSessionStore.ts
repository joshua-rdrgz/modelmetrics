import { createBroadcastMiddleware } from '@/features/current-session/stopwatch-store/stopwatchBroadcast';
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
  projectName: string;
  hourlyRate: number;
  events: StopwatchSessionEvent[];
  elapsedTime: number;
  isStopwatchRunning: boolean;
  isFinalizingSession: boolean;
}

interface StopwatchSessionStateUpdaters {
  setProjectName: (name: string) => void;
  setHourlyRate: (rate: number) => void;
  addEvent: (type: StopwatchEventType) => Promise<void>;
  setIsStopwatchRunning: (isStopwatchRunning: boolean) => void;
  setElapsedTime: (time: number) => void;
  resetSession: () => void;
  setIsFinalizingSession: (isFinalizingSession: boolean) => void;
  /**
   * Determines if the stopwatch events are finished (i.e., the "finish" event has occurred).
   * This method is used to indicate when no more events can be added to the events array.
   * @returns {boolean} True if the stopwatch events are finished, false otherwise.
   */
  isStopwatchEventsFinished: () => boolean;
}

type StopwatchSessionStore = StopwatchSessionState &
  StopwatchSessionStateUpdaters;

const defaultStopwatchSessionState: StopwatchSessionState = {
  projectName: '',
  hourlyRate: 0,
  events: [],
  elapsedTime: 0,
  isStopwatchRunning: false,
  isFinalizingSession: false,
};

export const useStopwatchSessionStore = create<StopwatchSessionStore>()(
  devtools(
    persist(
      createBroadcastMiddleware('stopwatch-channel')(
        immer((set, get) => ({
          // ** STATE **
          ...defaultStopwatchSessionState,

          // ** STATE UPDATERS **
          setProjectName: (name) =>
            set(
              (state) => {
                state.projectName = name;
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true },
            ),
          setHourlyRate: (rate) =>
            set(
              (state) => {
                state.hourlyRate = rate;
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true },
            ),
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
          setIsStopwatchRunning: (isStopwatchRunning) =>
            set({ isStopwatchRunning }),
          setElapsedTime: (time) => set({ elapsedTime: time }),
          resetSession: () => set(defaultStopwatchSessionState),
          /**
           * Sets the state of the finalization dialog.
           * This method controls whether the finalization dialog is open or closed.
           * @param {boolean} isFinalizingSession - The new state of the finalization dialog.
           */
          setIsFinalizingSession: (isFinalizingSession) =>
            set(
              { isFinalizingSession },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true },
            ),
          isStopwatchEventsFinished: () => {
            const events = get().events;
            return (
              events.length > 0 && events[events.length - 1].type === 'finish'
            );
          },
        })) as StateCreator<StopwatchSessionStore, [['zustand/immer', never]]>,
      ),
      {
        name: 'stopwatch-session-storage',
        storage: createJSONStorage(() => localStorage),
      },
    ),
  ),
);
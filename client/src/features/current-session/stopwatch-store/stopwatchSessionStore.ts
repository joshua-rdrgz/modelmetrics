import { createBroadcastMiddleware } from '@/features/current-session/stopwatch-store/stopwatchBroadcast';
import { generateUniqueBrowserTabId } from '@/utils/generateUniqueBrowserTabId';
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
  activeDialogTabId: string | null;
  dialogTabId: string | null;
  finishEventTabId: string | null;
  sessionId: string | null;
  phase: 'capture' | 'refine';
}

interface StopwatchSessionStateUpdaters {
  setProjectName: (name: string) => void;
  setHourlyRate: (rate: number) => void;
  addEvent: (type: StopwatchEventType) => Promise<void>;
  updateEvent: (index: number, updatedEvent: StopwatchSessionEvent) => void;
  setIsStopwatchRunning: (isStopwatchRunning: boolean) => void;
  setElapsedTime: (time: number) => void;
  resetSession: () => void;
  setIsFinalizingSession: (isFinalizingSession: boolean) => void;
  setActiveDialogTabId: (tabId: string | null) => void;
  setDialogTabId: (tabId: string) => void;
  setFinishEventTabId: (tabId: string | null) => void;
  /**
   * Determines if the stopwatch events are finished (i.e., the "finish" event has occurred).
   * This method is used to indicate when no more events can be added to the events array.
   * @returns {boolean} True if the stopwatch events are finished, false otherwise.
   */
  isRefiningPhase: () => boolean;
}

type StopwatchSessionStore = StopwatchSessionState &
  StopwatchSessionStateUpdaters;

const defaultStopwatchSessionState: StopwatchSessionState = {
  projectName: '',
  hourlyRate: 40,
  events: [],
  elapsedTime: 0,
  isStopwatchRunning: false,
  isFinalizingSession: false,
  activeDialogTabId: null,
  dialogTabId: generateUniqueBrowserTabId(),
  finishEventTabId: null,
  sessionId: null,
  phase: 'capture',
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
              { broadcastChange: true, broadcastType: 'projectInfo' },
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
              { broadcastChange: true, broadcastType: 'projectInfo' },
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
                if (type === 'finish') {
                  state.isFinalizingSession = true;
                  state.finishEventTabId = state.dialogTabId;
                  state.sessionId = crypto.randomUUID();
                  state.phase = 'refine';
                }
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true, broadcastType: 'events' },
            ),
          updateEvent: (index: number, updatedEvent: StopwatchSessionEvent) =>
            set(
              (state) => {
                const newEvents = [...state.events];
                newEvents[index] = updatedEvent;
                const sortedEvents = newEvents.sort(
                  (a, b) => a.timestamp - b.timestamp,
                );
                return { events: sortedEvents };
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true, broadcastType: 'events' },
            ),
          setIsStopwatchRunning: (isStopwatchRunning) =>
            set({ isStopwatchRunning }),
          setElapsedTime: (time) => set({ elapsedTime: time }),
          resetSession: () =>
            set(
              (state) => {
                const newState = { ...defaultStopwatchSessionState };
                newState.dialogTabId = state.dialogTabId;
                return newState;
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              {
                broadcastChange: true,
                broadcastType: 'reset',
              },
            ),
          setIsFinalizingSession: (isFinalizingSession) =>
            set(
              { isFinalizingSession },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              {
                broadcastChange: true,
                broadcastType: 'isFinalizingSession',
              },
            ),
          setActiveDialogTabId: (tabId: string | null) =>
            set(
              (state) => {
                state.activeDialogTabId = tabId;
              },
              false,
              // **TODO**: Fix TypeScript bug here, info: https://github.com/pmndrs/zustand/issues/710
              // @ts-expect-error The original `set` function only expects 2 arguments,
              // but the custom broadcast middleware extends it to 3
              { broadcastChange: true, broadcastType: 'activeDialogTabId' },
            ),
          setDialogTabId: (dialogTabId) => set({ dialogTabId }),
          setFinishEventTabId: (finishEventTabId) => set({ finishEventTabId }),
          /**
           * Determines if the stopwatch events are finished (i.e., the "finish" event has occurred).
           * This method is used to indicate when no more events can be added to the events array.
           * @returns {boolean} True if the stopwatch events are finished, false otherwise.
           */
          isRefiningPhase: (): boolean => get().phase === 'refine',
        })) as StateCreator<StopwatchSessionStore, [['zustand/immer', never]]>,
      ),
      {
        name: 'stopwatch-session-storage',
        storage: createJSONStorage(() => localStorage),
        partialize: (state) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { dialogTabId: _, ...rest } = state;
          return rest;
        },
        onRehydrateStorage: () => (state) => {
          if (state) {
            const tabId = generateUniqueBrowserTabId();
            state.setDialogTabId(tabId);
          }
        },
      },
    ),
  ),
);

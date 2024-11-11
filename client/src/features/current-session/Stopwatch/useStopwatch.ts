import { useStopwatchSessionStore } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { stopwatchWorker } from '@/features/current-session/stopwatch-worker/stopwatchWorker';
import { calculateStopwatchTimeFromEvents } from '@/utils/calculateStopwatchTimeFromEvents';
import { useCallback, useEffect, useRef } from 'react';

export const useStopwatch = () => {
  const swStore = useStopwatchSessionStore();

  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (swStore.events.length === 0 || swStore.isRefiningPhase()) return;

    const lastEvent = swStore.events[swStore.events.length - 1];
    const timeFromEvents = calculateStopwatchTimeFromEvents(swStore.events);

    switch (lastEvent.type) {
      case 'start':
      case 'resume':
      case 'taskComplete':
        stopwatchWorker.start(timeFromEvents);
        swStore.setIsStopwatchRunning(true);
        intervalRef.current = setInterval(async () => {
          const time = await stopwatchWorker.getElapsedTime();
          swStore.setElapsedTime(time);
        }, 10);
        break;
      case 'break':
      case 'finish':
        stopwatchWorker.stop();
        swStore.setIsStopwatchRunning(false);
        if (intervalRef.current) clearInterval(intervalRef.current);
        break;
      default:
        console.error('Invalid event type in useStopwatch: ', lastEvent.type);
    }

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [swStore.events, swStore.setIsStopwatchRunning, swStore.setElapsedTime]);

  /**
   * Begins session.
   */
  const beginSession = useCallback(
    () => swStore.addEvent('start'),
    [swStore.addEvent],
  );

  /**
   * Pauses a running session.
   */
  const takeBreak = useCallback(
    () => swStore.addEvent('break'),
    [swStore.addEvent],
  );

  /**
   * Resumes a paused session.
   */
  const resumeSession = useCallback(
    () => swStore.addEvent('resume'),
    [swStore.addEvent],
  );

  /**
   * Marks a task completion event.  Does not stop stopwatch.
   */
  const markTaskCompletion = useCallback(
    () => swStore.addEvent('taskComplete'),
    [swStore.addEvent],
  );

  /**
   * Finalize session.
   */
  const finishSession = useCallback(
    () => swStore.addEvent('finish'),
    [swStore.addEvent],
  );

  return {
    ...swStore,
    beginSession,
    takeBreak,
    resumeSession,
    markTaskCompletion,
    finishSession,
  };
};

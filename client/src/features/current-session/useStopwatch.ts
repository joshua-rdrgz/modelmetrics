import {
  StopwatchSessionEvent,
  useStopwatchSessionStore,
} from '@/features/current-session/stopwatchSessionStore';
import { stopwatchWorker } from '@/features/current-session/stopwatchWorker';
import { calculateStopwatchTimeFromEvents } from '@/utils/calculateStopwatchTimeFromEvents';
import { useCallback, useEffect, useRef } from 'react';

interface UseStopwatchParams {
  onFinishEvent?(events: StopwatchSessionEvent[]): void;
}

const useStopwatch = (options: UseStopwatchParams = {}) => {
  const { onFinishEvent: onCustomFinishEvent = null } = options;
  const {
    events,
    elapsedTime,
    isRunning,
    addEvent,
    setIsRunning,
    setElapsedTime,
    resetSession,
  } = useStopwatchSessionStore();
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (events.length === 0) return;

    const lastEvent = events[events.length - 1];
    const timeFromEvents = calculateStopwatchTimeFromEvents(events);

    switch (lastEvent.type) {
      case 'start':
      case 'resume':
      case 'taskComplete':
        stopwatchWorker.start(timeFromEvents);
        setIsRunning(true);
        intervalRef.current = setInterval(async () => {
          const time = await stopwatchWorker.getElapsedTime();
          setElapsedTime(time);
        }, 10);
        break;
      case 'break':
        stopwatchWorker.stop();
        setIsRunning(false);
        if (intervalRef.current) clearInterval(intervalRef.current);
        break;
      case 'finish':
        stopwatchWorker.stop();
        setIsRunning(false);
        if (intervalRef.current) clearInterval(intervalRef.current);
        onCustomFinishEvent?.(events);
        resetSession();
        break;
      default:
        console.error('Invalid event type in useStopwatch: ', lastEvent.type);
    }

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [events, setElapsedTime, setIsRunning, onCustomFinishEvent, resetSession]);

  /**
   * Begins session.
   */
  const beginSession = useCallback(() => addEvent('start'), [addEvent]);

  /**
   * Pauses a running session.
   */
  const takeBreak = useCallback(() => addEvent('break'), [addEvent]);

  /**
   * Resumes a paused session.
   */
  const resumeSession = useCallback(() => addEvent('resume'), [addEvent]);

  /**
   * Marks a task completion event.  Does not stop stopwatch.
   */
  const markTaskCompletion = useCallback(
    () => addEvent('taskComplete'),
    [addEvent],
  );

  /**
   * Finalize session.
   */
  const finishSession = useCallback(() => addEvent('finish'), [addEvent]);

  return {
    isRunning,
    elapsedTime,
    beginSession,
    takeBreak,
    resumeSession,
    markTaskCompletion,
    finishSession,
  };
};

export default useStopwatch;

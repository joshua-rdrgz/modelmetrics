import { useStopwatchSessionStore } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { stopwatchWorker } from '@/features/current-session/stopwatch-worker/stopwatchWorker';
import { calculateStopwatchTimeFromEvents } from '@/utils/calculateStopwatchTimeFromEvents';
import { useCallback, useEffect, useRef } from 'react';

export const useStopwatch = () => {
  const {
    events,
    elapsedTime,
    isStopwatchRunning,
    projectName,
    hourlyRate,
    addEvent,
    setIsStopwatchRunning,
    setElapsedTime,
    setProjectName,
    setHourlyRate,
    setIsFinalizingSession,
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
        setIsStopwatchRunning(true);
        intervalRef.current = setInterval(async () => {
          const time = await stopwatchWorker.getElapsedTime();
          setElapsedTime(time);
        }, 10);
        break;
      case 'break':
        stopwatchWorker.stop();
        setIsStopwatchRunning(false);
        if (intervalRef.current) clearInterval(intervalRef.current);
        break;
      case 'finish':
        stopwatchWorker.stop();
        setIsStopwatchRunning(false);
        if (intervalRef.current) clearInterval(intervalRef.current);
        setIsFinalizingSession(true);
        break;
      default:
        console.error('Invalid event type in useStopwatch: ', lastEvent.type);
    }

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [events, setElapsedTime, setIsStopwatchRunning, setIsFinalizingSession]);

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
    isStopwatchRunning,
    elapsedTime,
    projectName,
    hourlyRate,
    setProjectName,
    setHourlyRate,
    beginSession,
    takeBreak,
    resumeSession,
    markTaskCompletion,
    finishSession,
  };
};

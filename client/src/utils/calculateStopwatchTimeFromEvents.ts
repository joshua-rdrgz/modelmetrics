import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

/**
 * Calculates the total elapsed time based on the session events.
 * @param {StopwatchSessionEvent[]} events - The array of session events.
 * @returns {number} The total elapsed time in milliseconds.
 */
export const calculateStopwatchTimeFromEvents = (
  events: StopwatchSessionEvent[],
): number => {
  let totalElapsedTime = 0;
  let lastStartTime: number | null = null;

  for (const event of events) {
    switch (event.type) {
      case 'start':
      case 'resume':
        if (lastStartTime === null) {
          lastStartTime = event.timestamp;
        }
        break;
      case 'break':
      case 'finish':
        if (lastStartTime !== null) {
          totalElapsedTime += event.timestamp - lastStartTime;
          lastStartTime = null;
        }
        break;
      case 'taskComplete':
        // Do nothing, as this doesn't affect the timer
        break;
    }
  }

  // If the timer is still running, add the time since the last start/resume
  if (lastStartTime !== null) {
    totalElapsedTime += Date.now() - lastStartTime;
  }

  return totalElapsedTime;
};

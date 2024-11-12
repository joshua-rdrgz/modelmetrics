import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

export const validateEvents = (events: StopwatchSessionEvent[]): boolean => {
  const sortedEvents = events.sort((a, b) => a.timestamp - b.timestamp);

  if (sortedEvents.length === 0) return false;

  // Check the first and last events
  if (
    sortedEvents[0].type !== 'start' ||
    sortedEvents[sortedEvents.length - 1].type !== 'finish'
  ) {
    return false;
  }

  let breakCount = 0;
  let resumeCount = 0;

  for (let i = 1; i < sortedEvents.length - 1; i++) {
    const event = sortedEvents[i];
    const nextEvent = sortedEvents[i + 1];

    switch (event.type) {
      case 'break':
        breakCount++;
        if (nextEvent.type !== 'resume') {
          return false;
        }
        break;
      case 'resume':
        resumeCount++;
        if (sortedEvents[i - 1].type !== 'break') {
          return false;
        }
        break;
      case 'taskComplete':
        if (sortedEvents[i - 1].type === 'break') {
          return false;
        }
        break;
      default:
        return false;
    }
  }

  // Check if the number of breaks and resumes are equal
  return breakCount === resumeCount;
};

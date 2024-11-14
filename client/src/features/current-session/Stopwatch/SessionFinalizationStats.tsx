import React, { useMemo } from 'react';
import * as C from '@/ui/card';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';

interface SessionFinalizationStatsProps {
  events: StopwatchSessionEvent[];
  isEventsValid: boolean;
}

interface SessionStats {
  totalTime: string;
  workTime: string;
  breakTime: string;
  tasksCompleted: string;
}

const formatDuration = (ms: number): string => {
  const { hours, minutes, seconds } = millisecondsToReadableTimer(ms);

  if (parseInt(hours) > 0) {
    return `${parseInt(hours)}h ${parseInt(minutes)}m ${parseInt(seconds)}s`;
  }

  if (parseInt(minutes) > 0) {
    return `${parseInt(minutes)}m ${parseInt(seconds)}s`;
  }

  return `${parseInt(seconds)}s`;
};

const calculateStats = (events: StopwatchSessionEvent[]): SessionStats => {
  const totalMs = events[events.length - 1].timestamp - events[0].timestamp;
  const taskCount = events.filter(
    (event) => event.type === 'taskComplete',
  ).length;

  let breakMs = 0;
  for (let i = 0; i < events.length; i++) {
    if (events[i].type === 'break') {
      breakMs += events[i + 1].timestamp - events[i].timestamp;
    }
  }

  const workMs = totalMs - breakMs;

  return {
    totalTime: formatDuration(totalMs),
    workTime: formatDuration(workMs),
    breakTime: formatDuration(breakMs),
    tasksCompleted: taskCount.toString(),
  };
};

export const SessionFinalizationStats: React.FC<
  SessionFinalizationStatsProps
> = ({ events, isEventsValid }) => {
  const stats = useMemo(
    () => (isEventsValid ? calculateStats(events) : null),
    [events, isEventsValid],
  );

  const statsConfig = [
    { title: 'Total Time', value: stats?.totalTime },
    { title: 'Work Time', value: stats?.workTime },
    { title: 'Break Time', value: stats?.breakTime },
    { title: 'Tasks Completed', value: stats?.tasksCompleted },
  ];

  return (
    <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
      {statsConfig.map(({ title, value }) => (
        <C.Root key={title} className='bg-muted/50'>
          <C.Header>
            <C.Title>{title}</C.Title>
          </C.Header>
          <C.Content>
            <p className='text-2xl font-bold'>{value ?? '-'}</p>
          </C.Content>
        </C.Root>
      ))}
    </div>
  );
};

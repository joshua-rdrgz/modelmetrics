import React from 'react';
import Stopwatch from '@/features/current-session/Stopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatchSessionStore';

export const CurrentSessionPage: React.FC = () => {
  const handleFinishEvent1 = (events: StopwatchSessionEvent[]) => {
    console.log('Current Session Page: Finish event 1 triggered');
    console.log('Events:', events);
  };

  return (
    <div className='container mx-auto mt-8'>
      <h1 className='text-2xl font-bold mb-4'>Current Session</h1>
      <Stopwatch.Root onFinishEvent={handleFinishEvent1}>
        <Stopwatch.Display />
        <Stopwatch.Actions />
      </Stopwatch.Root>
    </div>
  );
};

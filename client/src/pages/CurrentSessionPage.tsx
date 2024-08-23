import { SW } from '@/features/current-session/Stopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

export const CurrentSessionPage: React.FC = () => {
  const handleFinishEvent = (events: StopwatchSessionEvent[]) => {
    console.log('Current Session Page: Finish event triggered');
    console.log('Events:', events);
  };

  return (
    <div className='container mx-auto mt-8'>
      <h1 className='text-2xl font-bold mb-4'>Current Session</h1>
      <SW.Root onFinishEvent={handleFinishEvent}>
        <SW.SessionName />
        <SW.Display />
        <SW.Actions />
      </SW.Root>
    </div>
  );
};

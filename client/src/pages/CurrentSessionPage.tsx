import { SW } from '@/features/current-session/Stopwatch';
import { FinalizationEventData } from '@/features/current-session/Stopwatch/StopwatchRoot';

export const CurrentSessionPage: React.FC = () => {
  const handleFinalizeSession = (data: FinalizationEventData) => {
    console.log('Current Session Page: Finish event triggered');
    console.log('Events:', data);
  };

  return (
    <div className='container mx-auto mt-8'>
      <h1 className='text-2xl font-bold mb-4'>Current Session</h1>
      <SW.Root onFinalizeSession={handleFinalizeSession}>
        <SW.SessionName />
        <SW.Display />
        <SW.Actions />
      </SW.Root>
    </div>
  );
};

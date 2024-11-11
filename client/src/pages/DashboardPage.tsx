import { SW } from '@/features/current-session/Stopwatch';
import { FinalizationEventData } from '@/features/current-session/Stopwatch/StopwatchRoot';

export const DashboardPage = () => {
  const handleFinalizeSession = (data: FinalizationEventData) => {
    console.log('Dashboard Page: Finish event triggered');
    console.log('Events:', data);
  };

  return (
    <SW.Root onFinalizeSession={handleFinalizeSession}>
      <SW.Display />
      <SW.Actions />
      <SW.SessionName />
    </SW.Root>
  );
};

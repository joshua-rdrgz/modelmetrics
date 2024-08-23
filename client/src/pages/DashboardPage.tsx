import { SW } from '@/features/current-session/Stopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

export const DashboardPage = () => {
  const handleFinishEvent = (events: StopwatchSessionEvent[]) => {
    console.log('Dashboard Page: Finish event triggered');
    console.log('Events:', events);
  };

  return (
    <SW.Root onFinishEvent={handleFinishEvent}>
      <SW.Display />
      <SW.Actions />
      <SW.SessionName />
    </SW.Root>
  );
};

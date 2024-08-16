import Stopwatch from '@/features/current-session/Stopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatchSessionStore';

export const DashboardPage = () => {
  const handleFinishEvent = (events: StopwatchSessionEvent[]) => {
    console.log('Dashboard Page: Finish event triggered');
    console.log('Events:', events);
  };

  return (
    <Stopwatch.Root onFinishEvent={handleFinishEvent}>
      <Stopwatch.Display />
      <Stopwatch.Actions />
    </Stopwatch.Root>
  );
};

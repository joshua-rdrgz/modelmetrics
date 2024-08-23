import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { StopwatchContextProvider } from '@/features/current-session/Stopwatch/StopwatchContext';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';

type StopwatchRootProps = {
  children: React.ReactNode;
  onFinishEvent?: (events: StopwatchSessionEvent[]) => void;
};

export const StopwatchRoot: React.FC<StopwatchRootProps> = ({
  children,
  onFinishEvent,
}) => {
  const stopwatchState = useStopwatch({ onFinishEvent });
  const readableTimer = millisecondsToReadableTimer(stopwatchState.elapsedTime);

  return (
    <StopwatchContextProvider context={{ ...stopwatchState, ...readableTimer }}>
      <div className='flex flex-col items-center space-y-4'>{children}</div>
    </StopwatchContextProvider>
  );
};

import {
  SWSessionFinalizationData,
  SWSessionFinalizationDF,
} from '@/features/current-session/Stopwatch/SWSessionFinalizationDF';
import { StopwatchContextProvider } from '@/features/current-session/Stopwatch/StopwatchContext';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';

export type FinalizationEventData = {
  projectName: string;
  hourlyRate: number;
  events: StopwatchSessionEvent[];
};

type StopwatchRootProps = {
  children: React.ReactNode;
  onFinalizeSession?: (data: FinalizationEventData) => void;
};

export const StopwatchRoot: React.FC<StopwatchRootProps> = ({
  children,
  onFinalizeSession,
}) => {
  const stopwatchState = useStopwatch();
  const readableTimer = millisecondsToReadableTimer(stopwatchState.elapsedTime);

  const handleFinalize = (data: SWSessionFinalizationData) => {
    onFinalizeSession?.({ ...data, events: stopwatchState.events });
    stopwatchState.resetSession();
  };

  const handleCancel = () => {
    stopwatchState.setIsFinalizingSession(false);
  };

  return (
    <StopwatchContextProvider context={{ ...stopwatchState, ...readableTimer }}>
      <div className='flex flex-col items-center space-y-4'>{children}</div>
      <SWSessionFinalizationDF
        onSubmit={handleFinalize}
        onCancel={handleCancel}
      />
    </StopwatchContextProvider>
  );
};

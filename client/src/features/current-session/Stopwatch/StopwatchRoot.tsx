import React from 'react';
import { StopwatchContextProvider } from '@/features/current-session/Stopwatch/StopwatchContext';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';
import { SWSessionFinalizationDF } from '@/features/current-session/Stopwatch/SWSessionFinalizationDF';
import {
  useStopwatchSessionStore,
  StopwatchSessionEvent,
} from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

type StopwatchRootProps = {
  children: React.ReactNode;
  onFinishEvent?: (data: {
    projectName: string;
    hourlyRate: number;
    events: StopwatchSessionEvent[];
  }) => void;
};

export const StopwatchRoot: React.FC<StopwatchRootProps> = ({
  children,
  onFinishEvent,
}) => {
  const stopwatchState = useStopwatch();
  const readableTimer = millisecondsToReadableTimer(stopwatchState.elapsedTime);
  const { resetSession, setIsFinalizingSession, events } =
    useStopwatchSessionStore();

  const handleFinalize = (data: {
    projectName: string;
    hourlyRate: number;
  }) => {
    console.log('Session finalized:', { ...data, events });
    onFinishEvent?.({ ...data, events });
    resetSession();
  };

  const handleCancel = () => {
    setIsFinalizingSession(false);
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
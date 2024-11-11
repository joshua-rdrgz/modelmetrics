import {
  SWSessionFinalizationData,
  SWSessionFinalizationDF,
} from '@/features/current-session/Stopwatch/SWSessionFinalizationDF';
import { StopwatchContextProvider } from '@/features/current-session/Stopwatch/StopwatchContext';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { BroadcastMessage } from '@/features/current-session/stopwatch-store/stopwatchBroadcast';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';
import { useEffect } from 'react';

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

  /**
   * Sets active tab to null
   * When active tab is destroyed
   * (i.e. refreshed, closed, etc)
   */
  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.visibilityState === 'hidden') {
        stopwatchState.setActiveDialogTabId(null);
        const message: BroadcastMessage = {
          type: 'tabClosed',
          data: { activeDialogTabId: null },
        };
        new BroadcastChannel('stopwatch-channel').postMessage(message);
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, [stopwatchState.setActiveDialogTabId]);

  /**
   * Opens up finalization dialog
   * in the browser tab that called
   * the "finish" event.
   */
  useEffect(() => {
    if (stopwatchState.finishEventTabId === stopwatchState.dialogTabId) {
      stopwatchState.setActiveDialogTabId(stopwatchState.dialogTabId);
      stopwatchState.setFinishEventTabId(null);
    }
  }, [stopwatchState.finishEventTabId, stopwatchState.dialogTabId]);

  const handleFinalize = (data: SWSessionFinalizationData) => {
    onFinalizeSession?.(data);
    stopwatchState.resetSession();
  };

  const handleCancel = () => {
    stopwatchState.setActiveDialogTabId(null);
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

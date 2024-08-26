import React from 'react';
import { useStopwatchContext } from '@/features/current-session/Stopwatch/StopwatchContext';
import { Button } from '@/ui/button';
import { useStopwatchSessionStore } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';

export const StopwatchActions: React.FC = () => {
  const {
    isStopwatchRunning,
    elapsedTime,
    beginSession,
    takeBreak,
    resumeSession,
    markTaskCompletion,
    finishSession,
  } = useStopwatchContext();
  const { setIsFinalizingSession, isStopwatchEventsFinished } =
    useStopwatchSessionStore();

  if (isStopwatchEventsFinished()) {
    return (
      <Button onClick={() => setIsFinalizingSession(true)}>
        Open Finalization Dialog
      </Button>
    );
  }

  return (
    <div className='flex space-x-2'>
      {!isStopwatchRunning && elapsedTime === 0 && (
        <Button onClick={beginSession}>🏁 Begin Session</Button>
      )}
      {isStopwatchRunning && (
        <>
          <Button onClick={takeBreak}>🛑 Take A Break</Button>
          <Button onClick={markTaskCompletion}>👨🏽‍💻 Mark Task Completion</Button>
        </>
      )}
      {!isStopwatchRunning && elapsedTime > 0 && (
        <Button onClick={resumeSession}>✅ Resume Session</Button>
      )}
      {isStopwatchRunning && elapsedTime > 0 && (
        <Button onClick={finishSession}>🎉 Finish</Button>
      )}
    </div>
  );
};

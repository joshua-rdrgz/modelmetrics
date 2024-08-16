import {
  useStopwatchContext,
  StopwatchContextProvider,
} from '@/features/current-session/stopwatchContext';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatchSessionStore';
import useStopwatch from '@/features/current-session/useStopwatch';
import { Button } from '@/ui/button';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';

type StopwatchRootProps = {
  children: React.ReactNode;
  onFinishEvent?: (events: StopwatchSessionEvent[]) => void;
};

const StopwatchRoot: React.FC<StopwatchRootProps> = ({
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

const Display: React.FC = () => {
  const { hours, minutes, seconds, centiseconds } = useStopwatchContext();
  return (
    <div className='text-6xl md:text-8xl font-mono tracking-tight flex'>
      <span className='w-[2ch]'>{hours}</span>
      <span>:</span>
      <span className='w-[2ch]'>{minutes}</span>
      <span>:</span>
      <span className='w-[2ch]'>{seconds}</span>
      <span>.</span>
      <span className='w-[2ch]'>{centiseconds}</span>
    </div>
  );
};

const Actions: React.FC = () => {
  const {
    isRunning,
    elapsedTime,
    beginSession,
    takeBreak,
    resumeSession,
    markTaskCompletion,
    finishSession,
  } = useStopwatchContext();

  return (
    <div className='flex space-x-2'>
      {!isRunning && elapsedTime === 0 && (
        <Button onClick={beginSession}>🏁 Begin Session</Button>
      )}
      {isRunning && (
        <>
          <Button onClick={takeBreak}>🛑 Take A Break</Button>
          <Button onClick={markTaskCompletion}>👨🏽‍💻 Mark Task Completion</Button>
        </>
      )}
      {!isRunning && elapsedTime > 0 && (
        <Button onClick={resumeSession}>✅ Resume Session</Button>
      )}
      {isRunning && elapsedTime > 0 && (
        <Button onClick={finishSession}>🎉 Finish</Button>
      )}
    </div>
  );
};

const Stopwatch = {
  Root: StopwatchRoot,
  Display,
  Actions,
};

export default Stopwatch;

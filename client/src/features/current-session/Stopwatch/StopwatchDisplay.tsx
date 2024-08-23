import { useStopwatchContext } from '@/features/current-session/Stopwatch/StopwatchContext';

export const StopwatchDisplay: React.FC = () => {
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

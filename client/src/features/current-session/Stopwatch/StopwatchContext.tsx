import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { millisecondsToReadableTimer } from '@/utils/millisecondsToReadableTimer';
import { createContext, useContext } from 'react';

type StopwatchContextType = ReturnType<typeof useStopwatch> &
  ReturnType<typeof millisecondsToReadableTimer>;

const StopwatchContext = createContext<StopwatchContextType | undefined>(
  undefined,
);

// eslint-disable-next-line react-refresh/only-export-components
export const useStopwatchContext = () => {
  const context = useContext(StopwatchContext);
  if (context === undefined) {
    throw new Error('useStopwatchContext must be used within a StopwatchRoot');
  }
  return context;
};

export const StopwatchContextProvider = ({
  children,
  context,
}: React.PropsWithChildren<{
  context: StopwatchContextType;
}>) => {
  return (
    <StopwatchContext.Provider value={context}>
      {children}
    </StopwatchContext.Provider>
  );
};

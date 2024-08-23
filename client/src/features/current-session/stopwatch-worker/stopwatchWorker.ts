import * as Comlink from 'comlink';

type StopwatchWorker = {
  start: (initialElapsedTime: number) => void;
  stop: () => void;
  getElapsedTime: () => Promise<number>;
};

const worker = new Worker(new URL('./stopwatch.worker.ts', import.meta.url), {
  type: 'module',
});

export const stopwatchWorker = Comlink.wrap<StopwatchWorker>(worker);

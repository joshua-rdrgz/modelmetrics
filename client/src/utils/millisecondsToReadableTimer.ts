import {
  HOUR_IN_MILLIS,
  MINUTE_IN_MILLIS,
  SECOND_IN_MILLIS,
  CENTISECOND_IN_MILLIS,
} from '@/utils/constants';

export const millisecondsToReadableTimer = (time: number) => {
  const hours = Math.floor(time / HOUR_IN_MILLIS);
  const minutes = Math.floor((time % HOUR_IN_MILLIS) / MINUTE_IN_MILLIS);
  const seconds = Math.floor((time % MINUTE_IN_MILLIS) / SECOND_IN_MILLIS);
  const centiseconds = Math.floor(
    (time % SECOND_IN_MILLIS) / CENTISECOND_IN_MILLIS,
  );

  return {
    hours: hours.toString().padStart(2, '0'),
    minutes: minutes.toString().padStart(2, '0'),
    seconds: seconds.toString().padStart(2, '0'),
    centiseconds: centiseconds.toString().padStart(2, '0'),
  };
};

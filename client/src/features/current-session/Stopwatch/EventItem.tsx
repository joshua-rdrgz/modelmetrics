import React, { useState, useCallback, useMemo } from 'react';
import { Input } from '@/ui/input';
import * as S from '@/ui/select';
import { Button } from '@/ui/button';
import {
  StopwatchEventType,
  StopwatchSessionEvent,
} from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { format, parse } from 'date-fns';

interface EventItemProps {
  event: StopwatchSessionEvent;
  index: number;
  onChange: (updatedEvent: StopwatchSessionEvent) => void;
  isEditing: boolean;
  onEditToggle: () => void;
  isEditDisabled: boolean;
  style: React.CSSProperties;
  allEvents: StopwatchSessionEvent[];
}

const formatTimestamp = (timestamp: number) => {
  return format(new Date(timestamp), "yyyy-MM-dd'T'HH:mm:ss");
};

const parseTimestamp = (dateString: string) => {
  return parse(dateString, "yyyy-MM-dd'T'HH:mm:ss", new Date()).getTime();
};

export const EventItem: React.FC<EventItemProps> = ({
  event,
  index,
  onChange,
  isEditing,
  onEditToggle,
  isEditDisabled,
  style,
  allEvents,
}) => {
  const [localEvent, setLocalEvent] = useState(event);

  const potentialPosition = useMemo(() => {
    const position = allEvents.filter(
      (e) => e.timestamp <= localEvent.timestamp,
    ).length;
    return position === 0 ? 1 : position;
  }, [allEvents, localEvent.timestamp]);

  const handleTypeChange = useCallback((type: StopwatchEventType) => {
    setLocalEvent((prev) => ({ ...prev, type }));
  }, []);

  const handleTimestampChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const newTimestamp = parseTimestamp(e.target.value);
      if (!isNaN(newTimestamp)) {
        setLocalEvent((prev) => ({ ...prev, timestamp: newTimestamp }));
      }
    },
    [],
  );

  const handleSave = useCallback(() => {
    onChange(localEvent);
    onEditToggle();
  }, [localEvent, onChange, onEditToggle]);

  const handleCancel = useCallback(() => {
    setLocalEvent(event);
    onEditToggle();
  }, [event, onEditToggle]);

  return (
    <div style={{ ...style, padding: '16px' }}>
      <div className='flex justify-between space-x-3'>
        <S.Root
          value={localEvent.type}
          onValueChange={handleTypeChange}
          disabled={!isEditing}
        >
          <S.Trigger className='flex-grow'>
            <S.Value />
          </S.Trigger>
          <S.Content>
            {(isEditing
              ? ['start', 'break', 'resume', 'taskComplete', 'finish']
              : [localEvent.type]
            ).map((type) => (
              <S.Item key={type} value={type}>
                {type.charAt(0).toUpperCase() + type.slice(1)}
              </S.Item>
            ))}
          </S.Content>
        </S.Root>
        <Input
          type='datetime-local'
          value={formatTimestamp(localEvent.timestamp)}
          onChange={handleTimestampChange}
          disabled={!isEditing}
          step='1'
          className='max-w-min'
        />
      </div>
      {isEditing && (
        <p className='text-sm text-yellow-500 mt-1 w-full text-center'>
          Be careful adjusting this material, as it can lead to bad session
          data!
        </p>
      )}
      <div className='mt-2 flex justify-end'>
        {isEditing ? (
          <div className='w-full flex space-x-2'>
            <Button
              type='button'
              variant='outline'
              onClick={handleCancel}
              wrapperClassName='w-1/2'
              className='w-full'
            >
              Cancel
            </Button>
            <Button
              type='button'
              onClick={handleSave}
              wrapperClassName='w-1/2'
              className='w-full'
            >
              Save as Event #{potentialPosition}
            </Button>
          </div>
        ) : (
          <Button
            type='button'
            variant='outline'
            onClick={onEditToggle}
            wrapperClassName='w-full'
            className='w-full'
            disabled={isEditDisabled}
          >
            Edit Event #{index + 1}
          </Button>
        )}
      </div>
    </div>
  );
};

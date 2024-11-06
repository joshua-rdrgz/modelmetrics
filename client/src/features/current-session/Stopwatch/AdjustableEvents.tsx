import React, { useCallback } from 'react';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { FixedSizeList as List } from 'react-window';
import { EventItem } from './EventItem';

interface AdjustableEventsProps {
  events: StopwatchSessionEvent[];
  onEventChange: (index: number, updatedEvent: StopwatchSessionEvent) => void;
  editingEventIndex: number | null;
  setEditingEventIndex: (index: number | null) => void;
}

export const AdjustableEvents: React.FC<AdjustableEventsProps> = ({
  events,
  onEventChange,
  editingEventIndex,
  setEditingEventIndex,
}) => {
  const renderEventItem = useCallback(
    ({ index, style }: { index: number; style: React.CSSProperties }) => (
      <EventItem
        key={index}
        event={events[index]}
        index={index}
        onChange={(updatedEvent) => onEventChange(index, updatedEvent)}
        isEditing={editingEventIndex === index}
        onEditToggle={() =>
          setEditingEventIndex(editingEventIndex === index ? null : index)
        }
        isEditDisabled={
          editingEventIndex !== null && editingEventIndex !== index
        }
        style={style}
        allEvents={events}
      />
    ),
    [events, onEventChange, editingEventIndex, setEditingEventIndex],
  );

  return (
    <div className='h-[400px] w-full border rounded-md overflow-hidden'>
      <List
        height={400}
        itemCount={events.length}
        itemSize={150}
        width='100%'
        className='scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100 dark:scrollbar-thumb-gray-600 dark:scrollbar-track-gray-800'
      >
        {renderEventItem}
      </List>
    </div>
  );
};

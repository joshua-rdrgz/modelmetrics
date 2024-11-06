import { AdjustableEvents } from '@/features/current-session/Stopwatch/AdjustableEvents';
import { SessionFinalizationStats } from '@/features/current-session/Stopwatch/SessionFinalizationStats';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { Button } from '@/ui/button';
import * as D from '@/ui/dialog';
import * as F from '@/ui/form';
import { Input } from '@/ui/input';
import { zodResolver } from '@hookform/resolvers/zod';
import React, { useCallback, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import * as z from 'zod';

const stopwatchFinalizationFormSchema = z.object({
  projectName: z.string().min(1, 'Project name is required'),
  hourlyRate: z.number().min(0, 'Hourly rate must be 0 or greater'),
});

export type SWSessionFinalizationData = z.infer<
  typeof stopwatchFinalizationFormSchema
> & { events: StopwatchSessionEvent[] };

interface SWSessionFinalizationDFProps {
  onSubmit: (data: SWSessionFinalizationData) => void;
  onCancel: () => void;
}

export const SWSessionFinalizationDF: React.FC<
  SWSessionFinalizationDFProps
> = ({ onSubmit, onCancel }) => {
  const {
    projectName,
    hourlyRate,
    events,
    isFinalizingSession,
    setIsFinalizingSession,
    isRefiningPhase,
    activeDialogTabId,
    dialogTabId,
    updateEvent,
  } = useStopwatch();

  const [activePage, setActivePage] = useState<'stats' | 'events'>('stats');
  const [editingEventIndex, setEditingEventIndex] = useState<number | null>(
    null,
  );

  const form = useForm<Omit<SWSessionFinalizationData, 'events'>>({
    resolver: zodResolver(stopwatchFinalizationFormSchema),
    defaultValues: {
      projectName,
      hourlyRate,
    },
  });

  // Reset form when a new session starts
  useEffect(() => {
    if (isFinalizingSession) {
      form.reset({
        projectName,
        hourlyRate,
      });
    }
  }, [isFinalizingSession, projectName, hourlyRate, form]);

  const handleSubmit = useCallback(
    form.handleSubmit((data: Omit<SWSessionFinalizationData, 'events'>) => {
      const sortedEvents = [...events].sort(
        (a, b) => a.timestamp - b.timestamp,
      );
      onSubmit({ ...data, events: sortedEvents });
      setIsFinalizingSession(false);
    }),
    [form, onSubmit, setIsFinalizingSession, events],
  );

  const handleCancel = useCallback(() => {
    onCancel();
    setIsFinalizingSession(false);
  }, [onCancel, setIsFinalizingSession]);

  const handleOpenChange = useCallback(
    (open: boolean) => {
      if (!open) {
        handleCancel();
      }
    },
    [handleCancel],
  );

  const handleEventChange = useCallback(
    (index: number, updatedEvent: StopwatchSessionEvent) => {
      updateEvent(index, updatedEvent);
      setEditingEventIndex(null);
    },
    [updateEvent],
  );

  if (!isRefiningPhase()) {
    return null;
  }

  const isActiveTab = activeDialogTabId === dialogTabId;

  return (
    <D.Root
      open={isFinalizingSession && isActiveTab}
      onOpenChange={handleOpenChange}
    >
      <D.Content className='max-w-4xl'>
        <D.Header>
          <D.Title>Finalize Session</D.Title>
          <D.Description>
            Please review and confirm your session details.
          </D.Description>
        </D.Header>
        <F.Root formMethods={form} onSubmit={handleSubmit}>
          <F.Field
            control={form.control}
            name='projectName'
            render={({ field }) => (
              <F.Item>
                <F.Label>Project Name</F.Label>
                <F.Control>
                  <Input {...field} />
                </F.Control>
                <F.Message />
              </F.Item>
            )}
          />
          <F.Field
            control={form.control}
            name='hourlyRate'
            render={({ field }) => (
              <F.Item>
                <F.Label>Hourly Rate</F.Label>
                <F.Control>
                  <Input
                    type='number'
                    {...field}
                    onChange={(e) => field.onChange(parseFloat(e.target.value))}
                  />
                </F.Control>
                <F.Message />
              </F.Item>
            )}
          />
          <div className='mt-6 flex justify-end space-x-4'>
            <Button
              type='button'
              onClick={() => setActivePage('stats')}
              variant={activePage === 'stats' ? 'secondary' : 'outline'}
            >
              Session Event Stats
            </Button>
            <Button
              type='button'
              onClick={() => setActivePage('events')}
              variant={activePage === 'events' ? 'secondary' : 'outline'}
            >
              Adjust Session Events
            </Button>
          </div>
          {activePage === 'stats' && (
            <div className='mt-6'>
              <h3 className='text-lg font-semibold mb-2'>
                Session Event Stats
              </h3>
              <D.Description className='mb-4'>
                Overview of your session statistics. Review total time, work
                time, breaks, and completed tasks.
              </D.Description>
              <SessionFinalizationStats />
            </div>
          )}
          {activePage === 'events' && (
            <div className='mt-6'>
              <h3 className='text-lg font-semibold mb-2'>
                Adjust Session Events
              </h3>
              <D.Description className='mb-4'>
                Review and modify your session events. Adjust timestamps or
                event types if needed.
              </D.Description>
              <AdjustableEvents
                events={events}
                onEventChange={handleEventChange}
                editingEventIndex={editingEventIndex}
                setEditingEventIndex={setEditingEventIndex}
              />
            </div>
          )}
          <D.Footer className='mt-6'>
            <Button type='button' variant='outline' onClick={handleCancel}>
              Cancel
            </Button>
            <Button type='submit' disabled={editingEventIndex !== null}>
              Submit
            </Button>
          </D.Footer>
        </F.Root>
      </D.Content>
    </D.Root>
  );
};

import { AdjustableEvents } from '@/features/current-session/Stopwatch/AdjustableEvents';
import { SessionFinalizationStats } from '@/features/current-session/Stopwatch/SessionFinalizationStats';
import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { StopwatchSessionEvent } from '@/features/current-session/stopwatch-store/stopwatchSessionStore';
import { Button } from '@/ui/button';
import * as C from '@/ui/card';
import * as D from '@/ui/dialog';
import * as F from '@/ui/form';
import { Input } from '@/ui/input';
import * as SA from '@/ui/scroll-area';
import * as T from '@/ui/tabs';
import { validateEvents } from '@/utils/validateEvents';
import { zodResolver } from '@hookform/resolvers/zod';
import React, { useCallback, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import * as z from 'zod';

const stopwatchFinalizationFormSchema = z.object({
  projectName: z.string().min(1, 'Project name is required'),
  hourlyRate: z
    .number({ message: 'Hourly rate is required' })
    .min(0, 'Hourly rate must be 0 or greater'),
  events: z
    .array(
      z.object({
        type: z.enum(['start', 'break', 'resume', 'taskComplete', 'finish']),
        timestamp: z.number(),
      }),
    )
    .refine(validateEvents, {
      message: '**Events are not in the correct order or format!**',
    }),
});

export type SWSessionFinalizationData = z.infer<
  typeof stopwatchFinalizationFormSchema
>;

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
    setProjectName,
    setHourlyRate,
  } = useStopwatch();

  const [editingEventIndex, setEditingEventIndex] = useState<number | null>(
    null,
  );

  const form = useForm<SWSessionFinalizationData>({
    mode: 'onChange',
    resolver: zodResolver(stopwatchFinalizationFormSchema),
    defaultValues: {
      projectName,
      hourlyRate,
      events,
    },
  });

  // Triggers validation on every change
  useEffect(() => {
    const subscription = form.watch(() => {
      form.trigger('events');
    });
    return () => subscription.unsubscribe();
  }, [form]);

  // Reset form when a new session starts
  useEffect(() => {
    if (isFinalizingSession) {
      form.reset({
        projectName,
        hourlyRate,
        events,
      });
    }
  }, [isFinalizingSession, projectName, hourlyRate, events, form]);

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
      // Update React Hook Form
      const eventsToValidate = [...events];
      eventsToValidate[index] = updatedEvent;
      form.setValue('events', eventsToValidate);

      // Update Zustand Store
      updateEvent(index, updatedEvent);

      // Reset editing event state
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
      <D.Content className='max-w-4xl max-h-lg'>
        <D.Header>
          <D.Title>Finalize Session</D.Title>
          <D.Description>
            Please review and confirm your session details.
          </D.Description>
        </D.Header>
        <F.Root formMethods={form} onSubmit={handleSubmit}>
          <SA.Root className='h-[calc(75vh-200px)] pr-4 border rounded-lg p-4'>
            <F.Field
              control={form.control}
              name='projectName'
              render={({
                field: { onChange, ...field },
                fieldState: { error },
              }) => (
                <F.Item className='p-1'>
                  <div className='flex justify-between'>
                    <F.Label>Project Name</F.Label>
                    <F.Message />
                  </div>
                  <F.Control>
                    <Input
                      {...field}
                      error={!!error}
                      onChange={(e) => {
                        onChange(e);
                        setProjectName(e.target.value);
                      }}
                    />
                  </F.Control>
                </F.Item>
              )}
            />
            <F.Field
              control={form.control}
              name='hourlyRate'
              render={({
                field: { onChange, ...field },
                fieldState: { error },
              }) => (
                <F.Item className='p-1'>
                  <div className='flex justify-between'>
                    <F.Label>Hourly Rate</F.Label>
                    <F.Message />
                  </div>
                  <F.Control>
                    <Input
                      type='number'
                      error={!!error}
                      {...field}
                      onChange={(e) => {
                        onChange(parseFloat(e.target.value));
                        setHourlyRate(parseFloat(e.target.value));
                      }}
                    />
                  </F.Control>
                </F.Item>
              )}
            />
            <div className='mt-6'>
              <T.Root defaultValue='stats' className='w-full'>
                <div className='flex justify-end mb-4'>
                  <T.List>
                    <T.Trigger value='stats'>Session Event Stats</T.Trigger>
                    <T.Trigger value='events'>Adjust Session Events</T.Trigger>
                  </T.List>
                </div>
                {/* Events Error Alert */}
                {form.formState.errors.events && (
                  <div className='w-full bg-destructive text-destructive-foreground p-4 rounded-lg mb-4'>
                    <p className='font-bold text-center'>
                      {form.formState.errors.events.message}
                    </p>
                  </div>
                )}
                <T.Content value='stats' className='w-full'>
                  <C.Root>
                    <C.Header>
                      <C.Title>Session Event Stats</C.Title>
                      <C.Description>
                        Overview of your session statistics. Review total time,
                        work time, breaks, and completed tasks.
                      </C.Description>
                    </C.Header>
                    <C.Content>
                      <SessionFinalizationStats
                        events={events}
                        isEventsValid={!form.formState.errors.events}
                      />
                    </C.Content>
                  </C.Root>
                </T.Content>
                <T.Content value='events' className='w-full'>
                  <C.Root>
                    <C.Header>
                      <C.Title>Adjust Session Events</C.Title>
                      <C.Description>
                        Review and modify your session events. Adjust timestamps
                        or event types if needed.
                      </C.Description>
                    </C.Header>
                    <C.Content>
                      <AdjustableEvents
                        events={events}
                        onEventChange={handleEventChange}
                        editingEventIndex={editingEventIndex}
                        setEditingEventIndex={setEditingEventIndex}
                      />
                    </C.Content>
                  </C.Root>
                </T.Content>
              </T.Root>
            </div>
          </SA.Root>
          <D.Footer className='mt-6'>
            <Button type='button' variant='ghost' onClick={handleCancel}>
              Cancel
            </Button>
            <Button
              type='submit'
              variant='default'
              disabled={!form.formState.isValid || editingEventIndex !== null}
            >
              Submit
            </Button>
          </D.Footer>
        </F.Root>
      </D.Content>
    </D.Root>
  );
};

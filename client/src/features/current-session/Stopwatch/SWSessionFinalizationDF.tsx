import { useStopwatch } from '@/features/current-session/Stopwatch/useStopwatch';
import { Button } from '@/ui/button';
import * as D from '@/ui/dialog';
import * as F from '@/ui/form';
import { Input } from '@/ui/input';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import * as z from 'zod';

const stopwatchFinalizationFormSchema = z.object({
  projectName: z.string().min(1, 'Project name is required'),
  hourlyRate: z.number().min(0, 'Hourly rate must be 0 or greater'),
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
    isStopwatchEventsFinished,
    activeDialogTabId,
    dialogTabId,
  } = useStopwatch();

  const form = useForm<SWSessionFinalizationData>({
    resolver: zodResolver(stopwatchFinalizationFormSchema),
    defaultValues: {
      projectName,
      hourlyRate,
    },
  });

  /**
   * Form resets on state updates
   * from Zustand.
   */
  useEffect(() => {
    form.reset({ projectName, hourlyRate });
  }, [projectName, hourlyRate, form]);

  const handleSubmit = form.handleSubmit((data: SWSessionFinalizationData) => {
    onSubmit(data);
    setIsFinalizingSession(false);
  });

  const handleCancel = () => {
    onCancel();
    setIsFinalizingSession(false);
  };

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      handleCancel();
    }
  };

  if (!isStopwatchEventsFinished()) {
    return null;
  }

  const isActiveTab = activeDialogTabId === dialogTabId;

  return (
    <D.Root
      open={isFinalizingSession && isActiveTab}
      onOpenChange={handleOpenChange}
    >
      <D.Content>
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
          <div className='mt-4'>
            <h3 className='text-lg font-semibold'>Session Events</h3>
            <ul className='mt-2 space-y-2'>
              {events.map((event, index) => (
                <li key={index}>
                  {event.type} - {new Date(event.timestamp).toLocaleString()}
                </li>
              ))}
            </ul>
          </div>
          <D.Footer className='mt-6'>
            <Button type='button' variant='outline' onClick={handleCancel}>
              Cancel
            </Button>
            <Button type='submit'>Submit</Button>
          </D.Footer>
        </F.Root>
      </D.Content>
    </D.Root>
  );
};

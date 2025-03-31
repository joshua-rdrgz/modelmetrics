import { ChevronDown } from 'lucide-react';
import { SessionSummaryDto } from '@/api/session/types';
import * as T from '@/ui/table';

interface SessionHistoryTableProps {
  sessions: SessionSummaryDto[];
}

export function SessionHistoryTable({ sessions }: SessionHistoryTableProps) {
  return (
    <T.Root>
      <T.Header>
        <T.Row>
          <T.Head>
            <div className='flex items-center gap-2'>
              Date
              <ChevronDown className='h-4 w-4' />
            </div>
          </T.Head>
          <T.Head>Project</T.Head>
          <T.Head>Earnings</T.Head>
        </T.Row>
      </T.Header>
      <T.Body>
        {sessions.map((session) => (
          <T.Row key={session.id}>
            <T.Cell>{new Date(session.date).toLocaleDateString()}</T.Cell>
            <T.Cell>{session.projectName}</T.Cell>
            <T.Cell>
              ${session.grossEarnings.amount.toFixed(2)}{' '}
              {session.grossEarnings.currency}
            </T.Cell>
          </T.Row>
        ))}
        {sessions.length === 0 && (
          <T.Row>
            <T.Cell colSpan={3} className='text-center text-gray-500 py-8'>
              No sessions found
            </T.Cell>
          </T.Row>
        )}
      </T.Body>
    </T.Root>
  );
}

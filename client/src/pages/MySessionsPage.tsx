import { SessionSummaryDto } from '@/api/session/types';
import { SessionHistoryTable } from '@/features/sessions/components/SessionHistoryTable';

const mockSessions: SessionSummaryDto[] = [
  {
    id: '1',
    date: '2021-01-01',
    projectName: 'Project 1',
    grossEarnings: {
      amount: 100,
      currency: 'USD',
    },
  },
  {
    id: '2',
    date: '2021-01-02',
    projectName: 'Project 2',
    grossEarnings: {
      amount: 150,
      currency: 'USD',
    },
  },
  {
    id: '3',
    date: '2021-01-03',
    projectName: 'Project 3',
    grossEarnings: {
      amount: 200,
      currency: 'USD',
    },
  },
  {
    id: '4',
    date: '2021-01-04',
    projectName: 'Project 4',
    grossEarnings: {
      amount: 250,
      currency: 'USD',
    },
  },
];

export const MySessionsPage = () => {
  return (
    <div className='p-6'>
      <h1 className='mb-6 text-2xl font-bold'>Session History</h1>
      <SessionHistoryTable sessions={mockSessions} />
    </div>
  );
};

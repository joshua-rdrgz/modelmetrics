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
  {
    id: '5',
    date: '2021-01-05',
    projectName: 'Project 5',
    grossEarnings: {
      amount: 300,
      currency: 'USD',
    },
  },
  {
    id: '6',
    date: '2021-01-01',
    projectName: 'Project 1',
    grossEarnings: {
      amount: 100,
      currency: 'USD',
    },
  },
  {
    id: '7',
    date: '2021-01-02',
    projectName: 'Project 2',
    grossEarnings: {
      amount: 150,
      currency: 'USD',
    },
  },
  {
    id: '8',
    date: '2021-01-03',
    projectName: 'Project 3',
    grossEarnings: {
      amount: 200,
      currency: 'USD',
    },
  },
  {
    id: '9',
    date: '2021-01-04',
    projectName: 'Project 4',
    grossEarnings: {
      amount: 250,
      currency: 'USD',
    },
  },
  {
    id: '10',
    date: '2021-01-05',
    projectName: 'Project 5',
    grossEarnings: {
      amount: 300,
      currency: 'USD',
    },
  },
  {
    id: '11',
    date: '2021-01-01',
    projectName: 'Project 1',
    grossEarnings: {
      amount: 100,
      currency: 'USD',
    },
  },
  {
    id: '12',
    date: '2021-01-02',
    projectName: 'Project 2',
    grossEarnings: {
      amount: 150,
      currency: 'USD',
    },
  },
  {
    id: '13',
    date: '2021-01-03',
    projectName: 'Project 3',
    grossEarnings: {
      amount: 200,
      currency: 'USD',
    },
  },
  {
    id: '14',
    date: '2021-01-04',
    projectName: 'Project 4',
    grossEarnings: {
      amount: 250,
      currency: 'USD',
    },
  },
  {
    id: '15',
    date: '2021-01-05',
    projectName: 'Project 5',
    grossEarnings: {
      amount: 300,
      currency: 'USD',
    },
  },
  {
    id: '16',
    date: '2021-01-01',
    projectName: 'Project 1',
    grossEarnings: {
      amount: 100,
      currency: 'USD',
    },
  },
  {
    id: '17',
    date: '2021-01-02',
    projectName: 'Project 2',
    grossEarnings: {
      amount: 150,
      currency: 'USD',
    },
  },
  {
    id: '18',
    date: '2021-01-03',
    projectName: 'Project 3',
    grossEarnings: {
      amount: 200,
      currency: 'USD',
    },
  },
  {
    id: '19',
    date: '2021-01-04',
    projectName: 'Project 4',
    grossEarnings: {
      amount: 250,
      currency: 'USD',
    },
  },
  {
    id: '20',
    date: '2021-01-05',
    projectName: 'Project 5',
    grossEarnings: {
      amount: 300,
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

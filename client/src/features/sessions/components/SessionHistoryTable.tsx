import { SessionSummaryDto } from '@/api/session/types';
import * as T from '@/ui/table';
import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  getSortedRowModel,
  SortingState,
  useReactTable,
} from '@tanstack/react-table';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { useMemo, useState } from 'react';

interface SessionHistoryTableProps {
  sessions: SessionSummaryDto[];
}

export function SessionHistoryTable({ sessions }: SessionHistoryTableProps) {
  const [sorting, setSorting] = useState<SortingState>([
    { id: 'date', desc: true },
  ]);

  const columnHelper = createColumnHelper<SessionSummaryDto>();

  const columns = useMemo(
    () => [
      columnHelper.accessor('date', {
        header: 'Date',
        cell: (info) => new Date(info.getValue()).toLocaleDateString(),
      }),
      columnHelper.accessor('projectName', {
        header: 'Project',
        cell: (info) => info.getValue(),
      }),
      columnHelper.accessor('grossEarnings', {
        header: 'Earnings',
        cell: (info) => {
          const earnings = info.getValue();
          return (
            <span className='font-medium'>
              ${earnings.amount.toFixed(2)}{' '}
              <span className='text-muted-foreground text-xs'>
                {earnings.currency}
              </span>
            </span>
          );
        },
      }),
    ],
    [columnHelper],
  );

  const table = useReactTable({
    data: sessions,
    columns,
    state: {
      sorting,
    },
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  return (
    <div className='rounded-md border border-primary/20'>
      <T.Root>
        <T.Header className='bg-primary/20'>
          {table.getHeaderGroups().map((headerGroup) => (
            <T.Row key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <T.Head
                  key={header.id}
                  className={
                    header.column.getCanSort()
                      ? 'cursor-pointer select-none'
                      : ''
                  }
                  onClick={header.column.getToggleSortingHandler()}
                >
                  <div className='flex items-center gap-2'>
                    {flexRender(
                      header.column.columnDef.header,
                      header.getContext(),
                    )}
                    {header.column.getCanSort() && (
                      <div className='h-4 w-4'>
                        {header.column.getIsSorted() === 'asc' ? (
                          <ChevronUp className='h-4 w-4' />
                        ) : header.column.getIsSorted() === 'desc' ? (
                          <ChevronDown className='h-4 w-4' />
                        ) : (
                          <ChevronDown className='h-4 w-4 opacity-30' />
                        )}
                      </div>
                    )}
                  </div>
                </T.Head>
              ))}
            </T.Row>
          ))}
        </T.Header>
        <T.Body className='divide-y divide-primary/20'>
          {table.getRowModel().rows.length > 0 ? (
            table.getRowModel().rows.map((row) => (
              <T.Row key={row.id} className='hover:bg-muted/50'>
                {row.getVisibleCells().map((cell) => (
                  <T.Cell key={cell.id}>
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </T.Cell>
                ))}
              </T.Row>
            ))
          ) : (
            <T.Row>
              <T.Cell
                colSpan={columns.length}
                className='text-center text-muted-foreground py-10'
              >
                No sessions found
              </T.Cell>
            </T.Row>
          )}
        </T.Body>
      </T.Root>
    </div>
  );
}

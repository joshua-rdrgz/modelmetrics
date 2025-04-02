import { SessionSummaryDto } from '@/api/session/types';
import { DataTableColumnHeader } from '@/ui/DataTableColumnHeader';
import { DataTablePagination } from '@/ui/DataTablePagination';
import * as T from '@/ui/table';
import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  SortingState,
  useReactTable,
} from '@tanstack/react-table';
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
        header: ({ column }) => (
          <DataTableColumnHeader column={column} title='Date' />
        ),
        cell: (info) => new Date(info.getValue()).toLocaleDateString(),
      }),
      columnHelper.accessor('projectName', {
        header: ({ column }) => (
          <DataTableColumnHeader column={column} title='Project' />
        ),
        cell: (info) => info.getValue(),
      }),
      columnHelper.accessor('grossEarnings', {
        header: ({ column }) => (
          <DataTableColumnHeader column={column} title='Earnings' />
        ),
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
    getPaginationRowModel: getPaginationRowModel(),
  });

  return (
    <>
      <div className='rounded-md border border-primary/20 mb-4'>
        <T.Root>
          <T.Header className='bg-primary/20'>
            {table.getHeaderGroups().map((headerGroup) => (
              <T.Row key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <T.Head key={header.id}>
                    <div className='flex items-center gap-2'>
                      {flexRender(
                        header.column.columnDef.header,
                        header.getContext(),
                      )}
                    </div>
                  </T.Head>
                ))}
              </T.Row>
            ))}
          </T.Header>
          <T.Body>
            {table.getRowModel().rows.length > 0 ? (
              table.getRowModel().rows.map((row) => (
                <T.Row
                  key={row.id}
                  className='border-primary/20 hover:bg-muted/50'
                >
                  {row.getVisibleCells().map((cell) => (
                    <T.Cell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext(),
                      )}
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
      <DataTablePagination table={table} />
    </>
  );
}

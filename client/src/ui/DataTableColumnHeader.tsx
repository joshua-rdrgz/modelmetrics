import { cn } from '@/lib/utils';
import { Button } from '@/ui/button';
import * as DM from '@/ui/dropdown-menu';
import { Column } from '@tanstack/react-table';
import { ArrowDown, ArrowUp, ChevronsUpDown } from 'lucide-react';

interface DataTableColumnHeaderProps<TData, TValue>
  extends React.HTMLAttributes<HTMLDivElement> {
  column: Column<TData, TValue>;
  title: string;
}

export function DataTableColumnHeader<TData, TValue>({
  column,
  title,
  className,
}: DataTableColumnHeaderProps<TData, TValue>) {
  if (!column.getCanSort()) {
    return <div className={cn(className)}>{title}</div>;
  }

  return (
    <div className={cn('flex items-center space-x-2', className)}>
      <DM.Menu>
        <DM.MenuTrigger asChild>
          <Button
            variant='ghost'
            size='sm'
            className='-ml-3 h-8 data-[state=open]:bg-accent gap-1'
          >
            <span>{title}</span>
            {column.getIsSorted() === 'desc' ? (
              <ArrowDown size={16} />
            ) : column.getIsSorted() === 'asc' ? (
              <ArrowUp size={16} />
            ) : (
              <ChevronsUpDown size={16} />
            )}
          </Button>
        </DM.MenuTrigger>
        <DM.MenuContent align='start'>
          <DM.MenuItem
            className='gap-1'
            onClick={() => column.toggleSorting(false)}
          >
            <ArrowUp className='h-3.5 w-3.5 text-muted-foreground/70' />
            Asc
          </DM.MenuItem>
          <DM.MenuItem
            className='gap-1'
            onClick={() => column.toggleSorting(true)}
          >
            <ArrowDown className='h-3.5 w-3.5 text-muted-foreground/70' />
            Desc
          </DM.MenuItem>
        </DM.MenuContent>
      </DM.Menu>
    </div>
  );
}

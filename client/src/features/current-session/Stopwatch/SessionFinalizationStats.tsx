import React from 'react';
import * as C from '@/ui/card';

export const SessionFinalizationStats: React.FC = () => {
  return (
    <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
      <C.Root className='bg-muted/50'>
        <C.Header>
          <C.Title>Total Time</C.Title>
        </C.Header>
        <C.Content>
          <p className='text-2xl font-bold'>2h 30m</p>
        </C.Content>
      </C.Root>
      <C.Root className='bg-muted/50'>
        <C.Header>
          <C.Title>Work Time</C.Title>
        </C.Header>
        <C.Content>
          <p className='text-2xl font-bold'>2h 15m</p>
        </C.Content>
      </C.Root>
      <C.Root className='bg-muted/50'>
        <C.Header>
          <C.Title>Break Time</C.Title>
        </C.Header>
        <C.Content>
          <p className='text-2xl font-bold'>15m</p>
        </C.Content>
      </C.Root>
      <C.Root className='bg-muted/50'>
        <C.Header>
          <C.Title>Tasks Completed</C.Title>
        </C.Header>
        <C.Content>
          <p className='text-2xl font-bold'>3</p>
        </C.Content>
      </C.Root>
    </div>
  );
};

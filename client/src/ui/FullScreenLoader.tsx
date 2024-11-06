import { PacmanLoader } from 'react-spinners';
import { useTheme } from '@/config/theme-provider';

export const FullScreenLoader = () => {
  const { actualTheme } = useTheme();

  return (
    <div className='min-h-screen min-w-full flex justify-center items-center'>
      <PacmanLoader
        size={50}
        color={actualTheme === 'dark' ? '#ffffff' : '#000000'}
      />
    </div>
  );
};

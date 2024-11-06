import { PacmanLoader } from 'react-spinners';
import { useTheme } from '@/config/theme-provider';

export const ComponentLoader = () => {
  const { actualTheme } = useTheme();

  return (
    <div className='flex justify-center items-center p-32'>
      <PacmanLoader
        size={35}
        color={actualTheme === 'dark' ? '#ffffff' : '#000000'}
      />
    </div>
  );
};

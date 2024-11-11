import { ClientRoutes } from '@/config/router';
import { useTheme } from '@/config/theme-provider';
import { LogoutBtn } from '@/features/auth/LogoutBtn';
import { useUser } from '@/features/user/useUser';
import { ModeToggle } from '@/ui/mode-toggle';
import { cva } from 'class-variance-authority';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import logoBlack from '../../../public/logo-black.svg';
import logoWhite from '../../../public/logo-white.svg';

const navVariants = cva('p-3 rounded-sm w-full', {
  variants: {
    theme: {
      dark: [
        'hover:bg-slate-600',
        'data-[active=true]:bg-slate-600 data-[active=true]:hover:bg-slate-700',
      ],
      light: [
        'hover:bg-slate-100',
        'data-[active=true]:bg-slate-100 data-[active=true]:hover:bg-slate-200',
      ],
    },
  },
});

export const DashboardLayout = () => {
  const { data: user } = useUser();
  const { actualTheme } = useTheme();
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path: string) => location.pathname === path;

  const handleNavigation = (path: string) => {
    if (!isActive(path)) {
      navigate(path);
    }
  };

  return (
    <div className='grid grid-cols-[13rem_1fr] grid-rows-[auto_1fr] h-screen'>
      <header className='bg-background flex items-center gap-6 justify-between py-2 px-12 font-heading'>
        {user && <div>Welcome, {user.firstName}!</div>}
        <div className='flex items-center gap-6 py-2 pr-3'>
          <ModeToggle />
          <LogoutBtn />
        </div>
      </header>
      <aside className='row-span-full pt-16 px-4 flex flex-col gap-12 bg-background font-heading font-bold text-lg'>
        <div className='w-full px-1'>
          <img
            src={actualTheme === 'dark' ? logoWhite : logoBlack}
            className='w-full h-full'
          />
        </div>
        <nav>
          <ul className='flex flex-col gap-6 items-start'>
            {[
              { path: ClientRoutes.DASHBOARD, label: 'Dashboard' },
              { path: ClientRoutes.CURRENT_SESSION, label: 'Current Session' },
              { path: ClientRoutes.MY_SESSIONS, label: 'Sessions History' },
              { path: ClientRoutes.USER_SETTINGS, label: 'Settings' },
            ].map(({ path, label }) => (
              <li
                key={path}
                onClick={() => handleNavigation(path)}
                className={`${navVariants({ theme: actualTheme })} ${isActive(path) ? 'cursor-not-allowed' : 'cursor-pointer'}`}
                data-active={isActive(path)}
              >
                <span className='block w-full'>{label}</span>
              </li>
            ))}
          </ul>
        </nav>
      </aside>
      <main className='bg-secondary overflow-auto px-6 py-4 font-body'>
        <div className='max-w-5xl mx-auto flex flex-col gap-6'>
          <Outlet />
        </div>
      </main>
    </div>
  );
};

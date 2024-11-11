import { PrivateRoute } from '@/features/auth/PrivateRoute';
import { DashboardLayout } from '@/features/dashboard/DashboardLayout';
import { AppErrorPage } from '@/pages/AppErrorPage';
import { CurrentSessionPage } from '@/pages/CurrentSessionPage';
import { DashboardPage } from '@/pages/DashboardPage';
import { FullErrorPage } from '@/pages/FullErrorPage';
import { LoginPage } from '@/pages/LoginPage';
import { ResendVerificationPage } from '@/pages/ResendVerificationPage';
import { SignUpPage } from '@/pages/SignUpPage';
import { SignUpSuccessPage } from '@/pages/SignUpSuccessPage';
import { UserSettingsPage } from '@/pages/UserSettingsPage';
import { VerifyEmailChangePage } from '@/pages/VerifyEmailChangePage';
import { VerifyPage } from '@/pages/VerifyPage';
import { RouteObject, redirect } from 'react-router-dom';

export enum ClientRoutes {
  SIGNUP = '/signup',
  LOGIN = '/login',
  SIGNUP_SUCCESS = '/signup-success',
  VERIFY = '/verify',
  RESEND_VERIFICATION = '/resend-verification',
  DASHBOARD = '/app/dashboard',
  CURRENT_SESSION = '/app/current-session',
  MY_SESSIONS = '/app/my-sessions',
  USER_SETTINGS = '/app/settings',
  VERIFY_EMAIL_CHANGE = '/verify-email-change',
}

export const routerConfig: RouteObject[] = [
  {
    index: true,
    loader: () => redirect(ClientRoutes.DASHBOARD),
  },
  {
    path: ClientRoutes.SIGNUP,
    element: <SignUpPage />,
  },
  {
    path: ClientRoutes.SIGNUP_SUCCESS,
    element: <SignUpSuccessPage />,
  },
  {
    path: ClientRoutes.LOGIN,
    element: <LoginPage />,
  },
  {
    path: ClientRoutes.VERIFY,
    element: <VerifyPage />,
  },
  {
    path: ClientRoutes.RESEND_VERIFICATION,
    element: <ResendVerificationPage />,
  },
  {
    path: ClientRoutes.VERIFY_EMAIL_CHANGE,
    element: <VerifyEmailChangePage />,
    errorElement: <FullErrorPage />,
  },
  {
    path: '/',
    element: <PrivateRoute />,
    errorElement: <FullErrorPage />,
    children: [
      {
        path: '/app',
        element: <DashboardLayout />,
        children: [
          {
            path: 'dashboard',
            element: <DashboardPage />,
            errorElement: <AppErrorPage />,
          },
          {
            path: 'current-session',
            element: <CurrentSessionPage />,
            errorElement: <AppErrorPage />,
          },
          {
            path: 'my-sessions',
            element: <div>My Sessions Page</div>,
          },
          {
            path: 'settings',
            element: <UserSettingsPage />,
            errorElement: <AppErrorPage />,
          },
        ],
      },
    ],
  },
];

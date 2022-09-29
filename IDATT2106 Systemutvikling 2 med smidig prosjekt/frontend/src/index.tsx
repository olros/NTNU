import { ReactElement, ReactNode, useEffect, lazy, Suspense } from 'react';
import ReactDOM from 'react-dom';
import 'assets/css/index.css';
import { useInterval } from 'hooks/Utils';
import { getCookie } from 'api/cookie';
import { ACCESS_TOKEN, REFRESH_TOKEN } from 'constant';
import { StylesProvider } from '@material-ui/core';
import CssBaseline from '@material-ui/core/CssBaseline';
import AdapterDateFns from '@material-ui/lab/AdapterDateFns';
import LocalizationProvider from '@material-ui/lab/LocalizationProvider';
import { Navigate, BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import URLS from 'URLS';
import 'delayed-scroll-restoration-polyfill';

// Services
import { ThemeProvider } from 'hooks/ThemeContext';
import { SnackbarProvider } from 'hooks/Snackbar';
import { useUser, useRefreshToken } from 'hooks/User';

// Project components
import Navigation from 'components/navigation/Navigation';

// Project containers
import Landing from 'containers/Landing';
const Http404 = lazy(() => import('containers/Http404'));
const About = lazy(() => import('containers/About'));
const Auth = lazy(() => import('containers/Auth'));
const Activities = lazy(() => import('containers/Activities'));
const ActivityDetails = lazy(() => import('containers/ActivityDetails'));
const ActivityAdmin = lazy(() => import('containers/ActivityAdmin'));
const Profile = lazy(() => import('containers/Profile'));
const Users = lazy(() => import('containers/Users'));

type AuthRouteProps = {
  path: string;
  element?: ReactElement | null;
  children?: ReactNode;
};

const AuthRoute = ({ children, path, element }: AuthRouteProps) => {
  const { data, isLoading } = useUser();

  if (isLoading) {
    return <Navigation isLoading noFooter />;
  } else if (!data) {
    return <Navigate to={URLS.LOGIN} />;
  } else {
    return (
      <Route element={element} path={path}>
        {children}
      </Route>
    );
  }
};

type ProvidersProps = {
  children: ReactNode;
};

export const Providers = ({ children }: ProvidersProps) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: 1000 * 60 * 2, // Don't refetch data before 2 min has passed
        refetchOnWindowFocus: false,
      },
    },
  });
  return (
    <QueryClientProvider client={queryClient}>
      <StylesProvider injectFirst>
        <ThemeProvider>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <CssBaseline />
            <SnackbarProvider>{children}</SnackbarProvider>
          </LocalizationProvider>
        </ThemeProvider>
      </StylesProvider>
      <ReactQueryDevtools />
    </QueryClientProvider>
  );
};

const AppRoutes = () => {
  const location = useLocation();
  const refreshToken = useRefreshToken();
  useEffect(() => {
    window.gtag('event', 'page_view', {
      page_location: window.location.href,
      page_path: window.location.pathname,
    });
  }, [location]);
  useInterval(() => {
    const access_token = getCookie(ACCESS_TOKEN);
    const refresh_token = getCookie(REFRESH_TOKEN);
    if (!access_token && refresh_token) {
      refreshToken.mutate(null);
    }
  }, 5000);
  return (
    <Routes>
      <Route element={<Landing />} path={URLS.LANDING} />
      <Route element={<About />} path={URLS.ABOUT} />
      <Route path={URLS.ACTIVITIES}>
        <Route element={<ActivityDetails />} path=':id/*' />
        <Route element={<Activities />} path='' />
      </Route>
      <Route element={<Auth />} path={`${URLS.LOGIN}*`} />
      <AuthRoute path={URLS.USERS}>
        <Route element={<Profile />} path=':userId/*' />
        <Route element={<Users />} path='' />
      </AuthRoute>
      <AuthRoute element={<Profile />} path={URLS.PROFILE} />
      <AuthRoute path={URLS.ADMIN_ACTIVITIES}>
        <Route element={<ActivityAdmin />} path=':activityId/' />
        <Route element={<ActivityAdmin />} path='' />
      </AuthRoute>

      <Route element={<Http404 />} path='*' />
    </Routes>
  );
};

export const Application = () => {
  return (
    <Providers>
      <BrowserRouter>
        <Suspense fallback={<Navigation isLoading />}>
          <AppRoutes />
        </Suspense>
      </BrowserRouter>
    </Providers>
  );
};

ReactDOM.render(<Application />, document.getElementById('root'));

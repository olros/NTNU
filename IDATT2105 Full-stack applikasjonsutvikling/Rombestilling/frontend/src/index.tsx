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
import { isUserAdmin } from 'utils';
import 'delayed-scroll-restoration-polyfill';

// Services
import { ThemeProvider } from 'hooks/ThemeContext';
import { SnackbarProvider } from 'hooks/Snackbar';
import { useUser, useRefreshToken } from 'hooks/User';

// Project components
import Navigation from 'components/navigation/Navigation';

// Project containers
import Landing from 'containers/Landing';
const Http404 = lazy(() => import(/* webpackChunkName: "http404" */ 'containers/Http404'));
const Auth = lazy(() => import(/* webpackChunkName: "auth" */ 'containers/Auth'));
const Groups = lazy(() => import(/* webpackChunkName: "groups" */ 'containers/Groups'));
const GroupDetails = lazy(() => import(/* webpackChunkName: "group_details" */ 'containers/GroupDetails'));
const Profile = lazy(() => import(/* webpackChunkName: "profile" */ 'containers/Profile'));
const Rooms = lazy(() => import(/* webpackChunkName: "rooms" */ 'containers/Rooms'));
const RoomDetails = lazy(() => import(/* webpackChunkName: "room_details" */ 'containers/RoomDetails'));
const Users = lazy(() => import(/* webpackChunkName: "users" */ 'containers/Users'));

type AuthRouteProps = {
  path: string;
  element?: ReactElement | null;
  children?: ReactNode;
  onlyAdmin?: boolean;
};

const AuthRoute = ({ children, path, element, onlyAdmin }: AuthRouteProps) => {
  const { data, isLoading } = useUser();

  if (isLoading) {
    return <Navigation isLoading />;
  } else if (!data || (onlyAdmin && !isUserAdmin(data))) {
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
      <Route element={<Auth />} path={`${URLS.LOGIN}*`} />
      <Route path='*'>
        <Navigation>
          <Routes>
            <Route element={<Landing />} path={URLS.LANDING} />
            <AuthRoute path={`${URLS.GROUPS}*`}>
              <Route element={<GroupDetails />} path=':id/' />
              <Route element={<Groups />} path='' />
            </AuthRoute>
            <AuthRoute path={`${URLS.ROOMS}*`}>
              <Route element={<RoomDetails />} path=':id/' />
              <Route element={<Rooms />} path='' />
            </AuthRoute>
            <AuthRoute element={<Profile />} path={URLS.PROFILE} />
            <AuthRoute onlyAdmin path={`${URLS.USERS}*`}>
              <Route element={<Profile />} path=':userId/*' />
              <Route element={<Users />} path='' />
            </AuthRoute>

            <Route element={<Http404 />} path='/*' />
          </Routes>
        </Navigation>
      </Route>
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

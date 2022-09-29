/* eslint-disable comma-spacing */
import { ReactElement, ReactNode } from 'react';
import { render } from '@testing-library/react';
import { act } from 'react-test-renderer';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ThemeProvider } from 'hooks/ThemeContext';
import { SnackbarProvider } from 'hooks/Snackbar';
import { BrowserRouter } from 'react-router-dom';
import { API_URL } from 'constant';
import nock from 'nock';
import { User, LoginRequestResponse, Activity, PaginationResponse, Post } from 'types/Types';
import { TrainingLevel } from 'types/Enums';

interface AllProvidersProps {
  children?: ReactNode;
}

const AllProviders = ({ children }: AllProvidersProps) => {
  const queryClient = new QueryClient();
  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <SnackbarProvider>{children}</SnackbarProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const customRender = (ui: ReactElement) => render(ui, { wrapper: AllProviders });

const headers = {
  'Access-Control-Allow-Origin': '*',
  'access-control-allow-headers': 'Authorization',
  'Content-Type': 'application/json',
};
const getRegex = (url: string) => new RegExp(`${url}.`, 'gi');

export const mockUser = (id?: string): User => ({
  birthDate: null,
  currentUserIsFollowing: false,
  email: 'test@example.com',
  firstName: 'Ola',
  followerCount: 1,
  followingCount: 2,
  id: id || '123',
  image: '',
  level: null,
  surname: 'Normann',
});

export const mockFeedPost = (id?: string): Post => ({
  activity: null,
  commentsCount: 0,
  content: 'Bli med!',
  createdAt: '2021-04-30T09:52:10Z',
  creator: {
    currentUserIsFollowing: false,
    email: 'test@example.com',
    firstName: 'Ola',
    id: '123',
    image: '',
    surname: 'Normann',
  },
  hasLiked: true,
  id: id || '123',
  image: '',
  likesCount: 2,
});

export const mockActivity = (id?: string): Activity => ({
  capacity: 10,
  closed: false,
  creator: {
    currentUserIsFollowing: false,
    email: 'test@example.com',
    firstName: 'Ola',
    id: '123',
    image: '',
    surname: 'Normann',
  },
  description: 'Bli med å spille fotball!',
  endDate: '2021-05-27T11:00:02Z',
  equipment: [
    { name: 'Mål', amount: 2 },
    { name: 'Fotball', amount: 1 },
  ],
  geoLocation: { lat: 59.94918338369839, lng: 10.73399265187215 },
  hasLiked: true,
  hosts: [],
  id: id || '123',
  images: [{ url: 'https://i.ibb.co/mb2SFcp/natur.jpg' }],
  inviteOnly: true,
  level: TrainingLevel.MEDIUM,
  likesCount: 2,
  registered: 3,
  signupEnd: '2021-05-20T09:34:00Z',
  signupStart: '2021-04-29T09:34:00Z',
  startDate: '2021-05-27T10:00:02Z',
  title: 'Fotball',
});

export const mockLoginResponse = (refreshToken: string, token: string): LoginRequestResponse => ({
  refreshToken,
  token,
});

export const generatePaginated = <Type,>(page: number, createObject: (id: string) => Type): PaginationResponse<Type> => {
  return {
    content: Array.from(Array(10).keys()).map((i) => createObject(String(i + 1))),
    number: page,
    empty: false,
    last: false,
    totalElements: 10,
    totalPages: 1,
  };
};

export const mockGet = <Type,>(url: string, response: Type) =>
  nock(API_URL).persist().intercept(getRegex(url), 'OPTIONS').reply(200, {}, headers).get(getRegex(url)).reply(200, response, headers);

export const mockGetPaginated = <Type,>(url: string, createObject: (id: string) => Type) =>
  nock(API_URL)
    .persist()
    .intercept(getRegex(url), 'OPTIONS')
    .reply(200, {}, headers)
    .get(getRegex(url))
    .reply(
      200,
      (uri) => {
        const newUrl = new URL(`${API_URL}${uri}`);
        const { page } = Object.fromEntries(newUrl.searchParams);
        return generatePaginated(Number(page), createObject);
      },
      headers,
    );

export const mockPost = <Type,>(url: string, response: Type) =>
  nock(API_URL).persist().intercept(getRegex(url), 'OPTIONS').reply(200, {}, headers).post(getRegex(url)).reply(200, response, headers);

export * from '@testing-library/react';
export { customRender as render, AllProviders, act };
export { renderHook } from '@testing-library/react-hooks';

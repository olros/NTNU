/* eslint-disable comma-spacing */
import { ReactElement, ReactNode } from 'react';
import { render } from '@testing-library/react';
import { act } from 'react-test-renderer';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ThemeProvider } from 'hooks/ThemeContext';
import { SnackbarProvider } from 'hooks/Snackbar';
import { BrowserRouter } from 'react-router-dom';
import { API_URL, ACCESS_TOKEN, REFRESH_TOKEN } from 'constant';
import nock from 'nock';
import { User, LoginRequestResponse, PaginationResponse } from 'types/Types';
import { UserRole } from 'types/Enums';

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

export const mockUser = (id?: string): User => {
  Object.defineProperty(window.document, 'cookie', {
    writable: true,
    value: `${REFRESH_TOKEN}=ey12345;${ACCESS_TOKEN}=123456789`,
  });
  return {
    phoneNumber: '99887766',
    email: 'test@example.com',
    firstName: 'Ola',
    id: id || '123',
    image: '',
    surname: 'Normann',
    expirationDate: '2025-01-01',
    roles: [
      {
        name: UserRole.ADMIN,
      },
      {
        name: UserRole.USER,
      },
    ],
  };
};

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

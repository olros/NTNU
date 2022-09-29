/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { mockUser, mockGet, mockPost, mockLoginResponse, renderHook, AllProviders, act } from 'test-utils';
import { USERS, ME, AUTH } from 'api/api';
import { useUser, useLogin } from 'hooks/User';
import { LoginRequestResponse, User } from 'types/Types';

test('can load signed in user', async () => {
  const user = mockUser();
  mockGet<User>(`${USERS}/${ME}`, user);
  const { result, waitFor } = renderHook(() => useUser(), { wrapper: AllProviders });

  await waitFor(() => result.current.isSuccess);
  expect(result.current.data).toEqual(user);
});

test('can load user by id', async () => {
  const userId = 'userId';
  const user = mockUser(userId);
  mockGet<User>(`${USERS}/${userId}`, user);
  const { result, waitFor } = renderHook(() => useUser(userId), { wrapper: AllProviders });

  await waitFor(() => result.current.isSuccess);
  expect(result.current.data).toEqual(user);
  expect(result.current.data!.id).toEqual(userId);
});

test('login', async () => {
  const new_refresh_token = 'refresh_token';
  const new_access_token = 'access_token';
  const response = mockLoginResponse(new_refresh_token, new_access_token);
  mockPost<LoginRequestResponse>(`${AUTH}/login`, response);
  const { result } = renderHook(() => useLogin(), { wrapper: AllProviders });
  await act(async () => {
    await result.current.mutate(
      { email: 'test@email.com', password: 'Password123' },
      {
        onSuccess: (data) => {
          expect(data.refreshToken).toEqual(new_refresh_token);
          expect(data.token).toEqual(new_access_token);
        },
      },
    );
  });
});

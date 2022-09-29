/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { mockActivity, mockGet, mockGetPaginated, renderHook, AllProviders, generatePaginated, act } from 'test-utils';
import { ACTIVITIES } from 'api/api';
import { useActivityById, useActivities } from 'hooks/Activities';
import { Activity } from 'types/Types';

test('load activity by id', async () => {
  const activityId = 'activityId';
  const activity = mockActivity(activityId);
  mockGet<Activity>(`${ACTIVITIES}/${activityId}`, activity);
  const { result, waitFor } = renderHook(() => useActivityById(activityId), { wrapper: AllProviders });

  await waitFor(() => result.current.isSuccess);
  expect(result.current.data).toEqual(activity);
  expect(result.current.data!.id).toEqual(activityId);
});

test('load paginated activities', async () => {
  const mock = mockGetPaginated<Activity>(`${ACTIVITIES}`, mockActivity);
  const { result, waitFor } = renderHook(() => useActivities(), { wrapper: AllProviders });

  await waitFor(() => result.current.isSuccess);

  expect(result.current.data?.pages).toStrictEqual([generatePaginated(0, mockActivity)]);

  await act(async () => await result.current.fetchNextPage());

  expect(result.current.data?.pages).toStrictEqual([generatePaginated(0, mockActivity), generatePaginated(1, mockActivity)]);

  mock.done();
});

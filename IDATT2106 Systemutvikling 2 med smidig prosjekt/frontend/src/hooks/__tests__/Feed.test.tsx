/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { mockFeedPost, mockGetPaginated, renderHook, AllProviders, generatePaginated, act } from 'test-utils';
import { POSTS } from 'api/api';
import { useFeed } from 'hooks/Feed';
import { Post } from 'types/Types';

test('load feed', async () => {
  const mock = mockGetPaginated<Post>(`${POSTS}`, mockFeedPost);
  const { result, waitFor } = renderHook(() => useFeed(), { wrapper: AllProviders });

  await waitFor(() => result.current.isSuccess);

  expect(result.current.data?.pages).toStrictEqual([generatePaginated(0, mockFeedPost)]);

  await act(async () => await result.current.fetchNextPage());

  expect(result.current.data?.pages).toStrictEqual([generatePaginated(0, mockFeedPost), generatePaginated(1, mockFeedPost)]);

  mock.done();
});

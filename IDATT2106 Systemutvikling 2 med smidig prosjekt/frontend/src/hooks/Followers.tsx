import { useMutation, useInfiniteQuery, useQueryClient, UseMutationResult } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { UserList, PaginationResponse, RequestResponse } from 'types/Types';
import { USER_QUERY_KEY } from 'hooks/User';
export const FOLLOWERS_QUERY_KEY = `followers`;
export const FOLLOWING_QUERY_KEY = `following`;

/**
 * Get all users which follows a user, paginated
 * @param userId - Id of user
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useFollowers = (userId?: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<UserList>, RequestResponse>(
    [FOLLOWERS_QUERY_KEY, userId, filters],
    ({ pageParam = 0 }) => API.getFollowers(userId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get all users which the user follows, paginated
 * @param userId - Id of user
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useFollowing = (userId?: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<UserList>, RequestResponse>(
    [FOLLOWING_QUERY_KEY, userId, filters],
    ({ pageParam = 0 }) => API.getFollowing(userId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Follow a user
 */
export const useFollowUser = (): UseMutationResult<RequestResponse, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((userId) => API.followUser(userId), {
    onSuccess: (_, userId) => {
      queryClient.invalidateQueries(FOLLOWING_QUERY_KEY);
      queryClient.invalidateQueries([FOLLOWERS_QUERY_KEY, userId]);
      queryClient.invalidateQueries([USER_QUERY_KEY, userId]);
    },
  });
};

/**
 * Unfollow a user
 * @param id - Id of user
 */
export const useUnfollowUser = (userId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.unfollowUser(userId), {
    onSuccess: () => {
      queryClient.invalidateQueries(FOLLOWING_QUERY_KEY);
      queryClient.invalidateQueries([FOLLOWERS_QUERY_KEY, userId]);
      queryClient.invalidateQueries([USER_QUERY_KEY, userId]);
    },
  });
};

/**
 * Remover a follower
 * @param id - Id of user
 */
export const useRemoveFollower = (userId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.removeFollower(userId), {
    onSuccess: () => {
      queryClient.invalidateQueries(FOLLOWERS_QUERY_KEY);
      queryClient.invalidateQueries([USER_QUERY_KEY, undefined]);
    },
  });
};

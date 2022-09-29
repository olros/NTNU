import { useMutation, useInfiniteQuery, useQuery, useQueryClient, UseMutationResult } from 'react-query';
import API from 'api/api';
import { Group, GroupCreate, PaginationResponse, RequestResponse, UserList } from 'types/Types';
import { getNextPaginationPage } from 'utils';

export const GROUP_QUERY_KEY = 'group';
export const GROUPS_QUERY_KEY = 'groups';
export const USER_GROUPS_QUERY_KEY = 'user_groups';
export const MEMBERSHIPS_QUERY_KEY = 'memberships';

/**
 * Get a specific group
 * @param groupId - Id of group
 */
export const useGroup = (groupId: string) => {
  return useQuery<Group, RequestResponse>([GROUP_QUERY_KEY, groupId], () => API.getGroup(groupId));
};

/**
 * Get a groups where the user is creator or member
 * @param userId - Id of user
 */
export const useUserGroups = (userId?: string) => {
  return useQuery<Array<Group>, RequestResponse>([USER_GROUPS_QUERY_KEY, userId], () => API.getUserGroups(userId));
};

/**
 * Get groups, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useGroups = (filters?: any) => {
  return useInfiniteQuery<PaginationResponse<Group>, RequestResponse>(
    [GROUPS_QUERY_KEY, filters],
    ({ pageParam = 0 }) => API.getGroups({ sort: 'name,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create a new group
 */
export const useCreateGroup = (): UseMutationResult<Group, RequestResponse, GroupCreate, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((group) => API.createGroup(group), {
    onSuccess: () => {
      queryClient.invalidateQueries(GROUPS_QUERY_KEY);
    },
  });
};

/**
 * Update a group
 * @param groupId - Id of group
 */
export const useUpdateGroup = (groupId: string): UseMutationResult<Group, RequestResponse, Partial<Group>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((group) => API.updateGroup(groupId, group), {
    onSuccess: (group) => {
      queryClient.invalidateQueries([GROUP_QUERY_KEY, undefined]);
      queryClient.invalidateQueries([GROUP_QUERY_KEY, group.id]);
      queryClient.invalidateQueries(GROUPS_QUERY_KEY);
    },
  });
};

/**
 * Delete a group
 * @param groupId - Id of group
 */
export const useDeleteGroup = (groupId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deleteGroup(groupId), {
    onSuccess: () => {
      queryClient.invalidateQueries([GROUP_QUERY_KEY, groupId]);
      queryClient.invalidateQueries(GROUPS_QUERY_KEY);
    },
  });
};

/**
 * Get membership to a group
 * @param groupId - Id of group
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useMemberships = (groupId: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<UserList>, RequestResponse>(
    [GROUP_QUERY_KEY, groupId, MEMBERSHIPS_QUERY_KEY, filters],
    ({ pageParam = 0 }) => API.getMemberships(groupId, { sort: 'firstName,surname,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create membership to group
 * @param groupId - Id of group
 */
export const useCreateMembership = (groupId: string): UseMutationResult<PaginationResponse<UserList>, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((email) => API.createMembership(groupId, email), {
    onSuccess: () => {
      queryClient.invalidateQueries([GROUP_QUERY_KEY, groupId, MEMBERSHIPS_QUERY_KEY]);
      queryClient.invalidateQueries(USER_GROUPS_QUERY_KEY);
    },
  });
};

/**
 * Batch create memberships to group
 * @param groupId - Id of group
 */
export const useBatchCreateMemberships = (groupId: string): UseMutationResult<RequestResponse, RequestResponse, File, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((file) => API.batchAddMembership(groupId, file), {
    onSuccess: () => {
      queryClient.invalidateQueries([GROUP_QUERY_KEY, groupId, MEMBERSHIPS_QUERY_KEY]);
      queryClient.invalidateQueries(USER_GROUPS_QUERY_KEY);
    },
  });
};

/**
 * Delete a membership to a group
 * @param groupId - Id of group
 */
export const useDeleteMembership = (groupId: string): UseMutationResult<RequestResponse, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((userId) => API.deleteMembership(groupId, userId), {
    onSuccess: () => {
      queryClient.invalidateQueries([GROUP_QUERY_KEY, groupId, MEMBERSHIPS_QUERY_KEY]);
      queryClient.invalidateQueries(USER_GROUPS_QUERY_KEY);
    },
  });
};

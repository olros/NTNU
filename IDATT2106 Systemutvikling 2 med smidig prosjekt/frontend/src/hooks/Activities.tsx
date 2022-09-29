import { useMutation, useInfiniteQuery, useQuery, useQueryClient, UseMutationResult } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { Activity, ActivityList, ActivityRequired, Like, UserList, PaginationResponse, Registration, RequestResponse } from 'types/Types';
export const ACTIVITIES_QUERY_KEY = `activities_list`;
export const ACTIVITIY_QUERY_KEY = `activity`;
export const MY_REGISTRATIONS = `my_registrations`;
export const MY_LIKED_REGISTRATIONS = `my_liked_registrations`;
export const ACTIVITIES_QUERY_KEY_REGISTRATION = `${ACTIVITIY_QUERY_KEY}_registrations`;
export const ACTIVITIES_HOSTS_QUERY_KEY = `${ACTIVITIY_QUERY_KEY}_hosts`;
export const ACTIVITIES_INVITED_USERS_QUERY_KEY = `${ACTIVITIY_QUERY_KEY}_invited_users`;
export const ACTIVITIES_LIKES_QUERY_KEY = `${ACTIVITIY_QUERY_KEY}_likes`;

/**
 * Get a specific activity
 * @param id - Id of activity
 */
export const useActivityById = (id: string) => {
  return useQuery<Activity, RequestResponse>([ACTIVITIY_QUERY_KEY, id], () => API.getActivity(id), { enabled: id !== '' });
};

/**
 * Get all activities, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useActivities = (filters?: any) => {
  return useInfiniteQuery<PaginationResponse<ActivityList>, RequestResponse>(
    [ACTIVITIES_QUERY_KEY, filters],
    ({ pageParam = 0 }) => API.getActivities({ ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get activities where the user is participating, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useMyParticipatingActivities = (userId?: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<ActivityList>, RequestResponse>(
    [ACTIVITIES_QUERY_KEY, MY_REGISTRATIONS, filters],
    ({ pageParam = 0 }) => API.getMyParticipatingActivities(userId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get activities where the user is participating, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useMyLikedActivities = (userId?: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<ActivityList>, RequestResponse>(
    [ACTIVITIES_QUERY_KEY, MY_LIKED_REGISTRATIONS, filters],
    ({ pageParam = 0 }) => API.getMyLikedActivities(userId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get activities where the user is host or creator, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useMyHostActivities = (filters?: any) => {
  return useInfiniteQuery<PaginationResponse<ActivityList>, RequestResponse>(
    [ACTIVITIES_QUERY_KEY, 'me_host', filters],
    ({ pageParam = 0 }) => API.getMyHostActivities({ ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create a new activity
 */
export const useCreateActivity = (): UseMutationResult<Activity, RequestResponse, ActivityRequired, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((newActivity: ActivityRequired) => API.createActivity(newActivity), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(ACTIVITIES_QUERY_KEY);
      queryClient.setQueryData([ACTIVITIY_QUERY_KEY, data.id], data);
    },
  });
};

/**
 * Update an activity
 * @param id - Id of activity
 */
export const useUpdateActivity = (id: string): UseMutationResult<Activity, RequestResponse, ActivityRequired, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((updatedActivity: ActivityRequired) => API.updateActivity(id, updatedActivity), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(ACTIVITIES_QUERY_KEY);
      queryClient.setQueryData([ACTIVITIY_QUERY_KEY, id], data);
    },
  });
};

/**
 * Delete an activity
 * @param id - Id of activity
 */
export const useDeleteActivity = (id: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deleteActivity(id), {
    onSuccess: () => {
      queryClient.invalidateQueries(ACTIVITIES_QUERY_KEY);
      queryClient.removeQueries([ACTIVITIY_QUERY_KEY, id]);
    },
  });
};

//////////////////////////////////
///////// Activity hosts /////////
//////////////////////////////////

/**
 * Get all host on an activity
 * @param activityId - Id of activity
 */
export const useActivityHostsById = (activityId: string) => {
  return useQuery<Array<UserList>, RequestResponse>([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_HOSTS_QUERY_KEY], () => API.getActivityHosts(activityId));
};

/**
 * Add host to an activity
 * @param activityId - Id of activity
 */
export const useAddActivityHost = (activityId: string): UseMutationResult<Array<UserList>, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((email) => API.addActivityHost(activityId, email), {
    onSuccess: (data) => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_HOSTS_QUERY_KEY]);
      queryClient.setQueryData([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_HOSTS_QUERY_KEY], data);
    },
  });
};

/**
 * Remove host from an activity
 * @param activityId - Id of activity
 */
export const useRemoveActivityHost = (activityId: string): UseMutationResult<Array<UserList>, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((hostId) => API.removeActivityHost(activityId, hostId), {
    onSuccess: (data) => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_HOSTS_QUERY_KEY]);
      queryClient.setQueryData([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_HOSTS_QUERY_KEY], data);
    },
  });
};

//////////////////////////////////
///// Activity registrations /////
//////////////////////////////////

/**
 * Get all registrations in an activity, paginated
 * @param activityId - Id of activity
 */
export const useActivityRegistrations = (activityId: string) => {
  return useInfiniteQuery<PaginationResponse<Registration>, RequestResponse>(
    [ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_QUERY_KEY_REGISTRATION],
    ({ pageParam = 0 }) => API.getRegistrations(activityId, { page: pageParam }),
  );
};

/**
 * Get a user's registration at an activity if it exists
 * @param activityId - Id of activity
 * @param userId - Id of user
 */
export const useActivityRegistration = (activityId: string, userId: string) => {
  return useQuery<Registration, RequestResponse>(
    [ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_QUERY_KEY_REGISTRATION, userId],
    () => API.getRegistration(activityId, userId),
    {
      enabled: userId !== '',
      retry: false,
    },
  );
};

/**
 * Create a registration in an activity
 * @param activityId - Id of activity
 */
export const useCreateActivityRegistration = (activityId: string): UseMutationResult<Registration, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((userId) => API.createRegistration(activityId, userId), {
    onSuccess: (data) => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIES_QUERY_KEY, MY_REGISTRATIONS]);
      queryClient.setQueryData([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_QUERY_KEY_REGISTRATION, data.user.id], data);
    },
  });
};

/**
 * Delete a registration in an activity
 * @param activityId - Id of activity
 */
export const useDeleteActivityRegistration = (activityId: string): UseMutationResult<RequestResponse, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((userId: string) => API.deleteRegistration(activityId, userId), {
    onSuccess: () => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_QUERY_KEY_REGISTRATION]);
      queryClient.invalidateQueries([ACTIVITIES_QUERY_KEY, MY_REGISTRATIONS]);
    },
  });
};

//////////////////////////////////
//////// Activity invites ////////
//////////////////////////////////

/**
 * Get activities where the signed in user is participating, paginated
 * @param activityId - Id of activity
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useActivityInvitedUsers = (activityId: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<UserList>, RequestResponse>(
    [ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_INVITED_USERS_QUERY_KEY, filters],
    ({ pageParam = 0 }) => API.getActivityInvitedUsers(activityId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Invite a user to join a private activity
 * @param activityId - Id of activity
 */
export const useAddActivityInvitedUser = (activityId: string): UseMutationResult<PaginationResponse<UserList>, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((email) => API.addActivityInvitedUser(activityId, email), {
    onSuccess: () => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_INVITED_USERS_QUERY_KEY]);
    },
  });
};

/**
 * Remove an invited user from an activity
 * @param activityId - Id of activity
 */
export const useRemoveActivityInvitedUser = (activityId: string): UseMutationResult<PaginationResponse<UserList>, RequestResponse, string, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((userId) => API.removeActivityInvitedUser(activityId, userId), {
    onSuccess: () => {
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_INVITED_USERS_QUERY_KEY]);
    },
  });
};

//////////////////////////////////
///////// Activity likes /////////
//////////////////////////////////

/**
 * Get a user's registration at an activity if it exists
 * @param activityId - Id of activity
 * @param userId - Id of user
 */
export const useActivityLike = (activityId: string, userId: string) => {
  return useQuery<Like, RequestResponse>([ACTIVITIY_QUERY_KEY, activityId, ACTIVITIES_LIKES_QUERY_KEY, userId], () => API.getActivityLike(activityId, userId), {
    enabled: userId !== '',
    retry: false,
  });
};

/**
 * Like an activity
 * @param activityId - Id of activity
 */
export const useCreateActivityLike = (activityId: string): UseMutationResult<Like, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.createActivityLike(activityId), {
    onSuccess: () => {
      queryClient.invalidateQueries([ACTIVITIES_QUERY_KEY, MY_LIKED_REGISTRATIONS]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
    },
  });
};

/**
 * Remove a like to an activity
 * @param activityId - Id of activity
 */
export const useRemoveActivityLike = (activityId: string): UseMutationResult<Like, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deleteActivityLike(activityId), {
    onSuccess: () => {
      queryClient.invalidateQueries([ACTIVITIES_QUERY_KEY, MY_LIKED_REGISTRATIONS]);
      queryClient.invalidateQueries([ACTIVITIY_QUERY_KEY, activityId]);
    },
  });
};

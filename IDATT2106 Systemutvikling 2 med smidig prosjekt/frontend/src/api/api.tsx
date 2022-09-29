/* eslint-disable @typescript-eslint/no-explicit-any */
import { IFetch } from 'api/fetch';
import { setCookie } from 'api/cookie';
import { ACCESS_TOKEN, ACCESS_TOKEN_DURATION, REFRESH_TOKEN, REFRESH_TOKEN_DURATION } from 'constant';
import { logout } from 'hooks/User';
import {
  Activity,
  ActivityList,
  ActivityRequired,
  FileUploadResponse,
  Like,
  LoginRequestResponse,
  PaginationResponse,
  Post,
  PostCreate,
  Registration,
  RequestResponse,
  RefreshTokenResponse,
  User,
  UserCreate,
  UserList,
  Comment,
} from 'types/Types';

export const USERS = 'users';
export const ME = 'me';
export const AUTH = 'auth';
export const POSTS = 'posts';
export const ACTIVITIES = 'activities';
export const REGISTRATIONS = 'registrations';
export const INVITES = 'invites';
export const HOSTS = 'hosts';
export const LIKES = 'likes';
export const FOLLOWERS = 'followers';
export const FOLLOWING = 'following';
export const COMMENTS = 'comments';

export default {
  // Auth
  createUser: (item: UserCreate) => IFetch<RequestResponse>({ method: 'POST', url: `${USERS}/`, data: item, withAuth: false, tryAgain: false }),
  authenticate: (email: string, password: string) =>
    IFetch<LoginRequestResponse>({
      method: 'POST',
      url: `${AUTH}/login`,
      data: { email, password },
      withAuth: false,
      tryAgain: false,
    }),
  forgotPassword: (email: string) =>
    IFetch<RequestResponse>({ method: 'POST', url: `${AUTH}/forgot-password/`, data: { email }, withAuth: false, tryAgain: false }),
  resetPassword: (email: string, newPassword: string, token: string) =>
    IFetch<RequestResponse>({ method: 'POST', url: `${AUTH}/reset-password/${token}/`, data: { email, newPassword }, withAuth: false, tryAgain: false }),
  refreshAccessToken: () =>
    IFetch<RefreshTokenResponse>({ method: 'GET', url: `${AUTH}/refresh-token/`, refreshAccess: true, withAuth: false })
      .then((tokens) => {
        setCookie(ACCESS_TOKEN, tokens.token, ACCESS_TOKEN_DURATION);
        setCookie(REFRESH_TOKEN, tokens.refreshToken, REFRESH_TOKEN_DURATION);
        return tokens;
      })
      .catch((e) => {
        logout();
        throw e;
      }),
  changePassword: (oldPassword: string, newPassword: string) =>
    IFetch<RequestResponse>({ method: 'POST', url: `${AUTH}/change-password/`, data: { oldPassword, newPassword } }),
  deleteUser: () => IFetch<RequestResponse>({ method: 'DELETE', url: `${USERS}/me/` }),

  // Feed
  getPost: (postId: string) => IFetch<Post>({ method: 'GET', url: `${POSTS}/${postId}/` }),
  getFeed: (filters?: any) => IFetch<PaginationResponse<Post>>({ method: 'GET', url: `${POSTS}/`, data: filters || {} }),
  createPost: (newPost: PostCreate) => IFetch<Post>({ method: 'POST', url: `${POSTS}/`, data: newPost }),
  updatePost: (postId: string, updatedPost: Partial<Post>) => IFetch<Post>({ method: 'PUT', url: `${POSTS}/${postId}/`, data: updatedPost }),
  deletePost: (postId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${POSTS}/${postId}/` }),

  // Post likes
  getPostLike: (postId: string, userId: string) => IFetch<Like>({ method: 'GET', url: `${POSTS}/${postId}/${LIKES}/${userId}/` }),
  createPostLike: (postId: string) => IFetch<Like>({ method: 'POST', url: `${POSTS}/${postId}/${LIKES}/` }),
  deletePostLike: (postId: string) => IFetch<Like>({ method: 'DELETE', url: `${POSTS}/${postId}/${LIKES}/` }),

  // Post comments
  getPostComments: (postId: string, filters?: any) =>
    IFetch<PaginationResponse<Comment>>({ method: 'GET', url: `${POSTS}/${postId}/${COMMENTS}/`, data: filters || {} }),
  createPostComment: (postId: string, item: Pick<Comment, 'comment'>) =>
    IFetch<Comment>({ method: 'POST', url: `${POSTS}/${postId}/${COMMENTS}/`, data: item }),
  deletePostComment: (postId: string, id: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${POSTS}/${postId}/${COMMENTS}/${id}/` }),
  editPostComment: (postId: string, id: string, item: Pick<Comment, 'comment'>) =>
    IFetch<Comment>({ method: 'PUT', url: `${POSTS}/${postId}/${COMMENTS}/${id}/`, data: item }),

  // Activity
  getActivity: (id: string) => IFetch<Activity>({ method: 'GET', url: `${ACTIVITIES}/${id}/` }),
  getActivities: (filters?: any) => IFetch<PaginationResponse<ActivityList>>({ method: 'GET', url: `${ACTIVITIES}/`, data: filters || {} }),
  getMyParticipatingActivities: (userId?: string, filters?: any) =>
    IFetch<PaginationResponse<ActivityList>>({ method: 'GET', url: `${USERS}/${userId || ME}/${REGISTRATIONS}/`, data: filters || {} }),
  getMyLikedActivities: (userId?: string, filters?: any) =>
    IFetch<PaginationResponse<ActivityList>>({ method: 'GET', url: `${USERS}/${userId || ME}/activity-likes/`, data: filters || {} }),
  getMyHostActivities: (filters?: any) => IFetch<PaginationResponse<ActivityList>>({ method: 'GET', url: `${USERS}/${ME}/${HOSTS}/`, data: filters || {} }),
  createActivity: (item: ActivityRequired) => IFetch<Activity>({ method: 'POST', url: `${ACTIVITIES}/`, data: item }),
  updateActivity: (id: string, item: ActivityRequired) => IFetch<Activity>({ method: 'PUT', url: `${ACTIVITIES}/${id}/`, data: item }),
  deleteActivity: (id: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${ACTIVITIES}/${id}/` }),

  // Activity hosts
  getActivityHosts: (id: string) => IFetch<Array<UserList>>({ method: 'GET', url: `${ACTIVITIES}/${id}/${HOSTS}/` }),
  addActivityHost: (id: string, email: string) => IFetch<Array<UserList>>({ method: 'POST', url: `${ACTIVITIES}/${id}/${HOSTS}/`, data: { email } }),
  removeActivityHost: (id: string, hostId: string) => IFetch<Array<UserList>>({ method: 'DELETE', url: `${ACTIVITIES}/${id}/${HOSTS}/${hostId}/` }),

  // Activity invites
  getActivityInvitedUsers: (activityId: string, filters?: any) =>
    IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${ACTIVITIES}/${activityId}/${INVITES}/`, data: filters || {} }),
  addActivityInvitedUser: (activityId: string, email: string) =>
    IFetch<PaginationResponse<UserList>>({ method: 'POST', url: `${ACTIVITIES}/${activityId}/${INVITES}/`, data: { email } }),
  removeActivityInvitedUser: (activityId: string, userId: string) =>
    IFetch<PaginationResponse<UserList>>({ method: 'DELETE', url: `${ACTIVITIES}/${activityId}/${INVITES}/${userId}/` }),

  // Activity registrations
  getRegistrations: (activityId: string, filters?: any) =>
    IFetch<PaginationResponse<Registration>>({ method: 'GET', url: `${ACTIVITIES}/${activityId}/${REGISTRATIONS}/`, data: filters || {} }),
  getRegistration: (activityId: string, userId: string) =>
    IFetch<Registration>({ method: 'GET', url: `${ACTIVITIES}/${activityId}/${REGISTRATIONS}/${userId}/` }),
  createRegistration: (activityId: string, userId: string) =>
    IFetch<Registration>({ method: 'POST', url: `${ACTIVITIES}/${activityId}/${REGISTRATIONS}/`, data: { id: userId } }),
  deleteRegistration: (activityId: string, userId: string) =>
    IFetch<RequestResponse>({ method: 'DELETE', url: `${ACTIVITIES}/${activityId}/${REGISTRATIONS}/${userId}/` }),

  // Activity likes
  getActivityLike: (activityId: string, userId: string) => IFetch<Like>({ method: 'GET', url: `${ACTIVITIES}/${activityId}/${LIKES}/${userId}/` }),
  createActivityLike: (activityId: string) => IFetch<Like>({ method: 'POST', url: `${ACTIVITIES}/${activityId}/${LIKES}/` }),
  deleteActivityLike: (activityId: string) => IFetch<Like>({ method: 'DELETE', url: `${ACTIVITIES}/${activityId}/${LIKES}/` }),

  // Followers
  getFollowers: (userId?: string, filters?: any) =>
    IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${USERS}/${userId || ME}/${FOLLOWERS}/`, data: filters || {} }),
  getFollowing: (userId?: string, filters?: any) =>
    IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${USERS}/${userId || ME}/${FOLLOWING}/`, data: filters || {} }),
  followUser: (userId: string) => IFetch<RequestResponse>({ method: 'POST', url: `${USERS}/${ME}/${FOLLOWING}/`, data: { id: userId } }),
  unfollowUser: (userId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${USERS}/${ME}/${FOLLOWING}/${userId}/` }),
  removeFollower: (userId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${USERS}/${ME}/${FOLLOWERS}/${userId}/` }),

  // User
  getUser: (userId?: string) => IFetch<User>({ method: 'GET', url: `${USERS}/${userId || ME}/` }),
  getUsers: (filters?: any) => IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${USERS}/`, data: filters || {} }),
  updateUser: (userId: string, item: Partial<User>) => IFetch<User>({ method: 'PUT', url: `${USERS}/${userId}/`, data: item }),

  // Upload file
  uploadFile: (file: File | Blob) =>
    IFetch<FileUploadResponse>({ method: 'POST', url: 'https://api.imgbb.com/1/upload?key=909df01fa93bd63405c9a36d662523f3', file, withAuth: false }),

  // Activity comments
  getActivityComments: (activityId: string, filters?: any) =>
    IFetch<PaginationResponse<Comment>>({ method: 'GET', url: `${ACTIVITIES}/${activityId}/${COMMENTS}/`, data: filters || {} }),
  createActivityComment: (activityId: string, item: Pick<Comment, 'comment'>) =>
    IFetch<Comment>({ method: 'POST', url: `${ACTIVITIES}/${activityId}/${COMMENTS}/`, data: item }),
  deleteActivityComment: (activityId: string, id: string) =>
    IFetch<RequestResponse>({ method: 'DELETE', url: `${ACTIVITIES}/${activityId}/${COMMENTS}/${id}/` }),
  editActivityComment: (activityId: string, id: string, item: Pick<Comment, 'comment'>) =>
    IFetch<Comment>({ method: 'PUT', url: `${ACTIVITIES}/${activityId}/${COMMENTS}/${id}/`, data: item }),
};

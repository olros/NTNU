/* eslint-disable @typescript-eslint/no-explicit-any */
import { IFetch } from 'api/fetch';
import { setCookie } from 'api/cookie';
import { ACCESS_TOKEN, ACCESS_TOKEN_DURATION, REFRESH_TOKEN, REFRESH_TOKEN_DURATION } from 'constant';
import { logout } from 'hooks/User';
import {
  FileUploadResponse,
  Group,
  GroupCreate,
  LoginRequestResponse,
  PaginationResponse,
  RequestResponse,
  RefreshTokenResponse,
  User,
  UserCreate,
  UserList,
  SectionCreate,
  SectionList,
  Section,
  Statistics,
  ReservationCreate,
  Reservation,
} from 'types/Types';

export const USERS = 'users';
export const ME = 'me';
export const AUTH = 'auth';
export const GROUPS = 'groups';
export const MEMBERSHIPS = 'memberships';
export const SECTIONS = 'sections';
export const RESERVATIONS = 'reservations';
export const STATISTICS = 'statistics';

export default {
  // Auth
  createUser: (item: UserCreate) => IFetch<RequestResponse>({ method: 'POST', url: `${USERS}/`, data: item, tryAgain: false }),
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
  resetPassword: (email: string, password: string, token: string) =>
    IFetch<RequestResponse>({ method: 'POST', url: `${AUTH}/reset-password/${token}/`, data: { email, password }, withAuth: false, tryAgain: false }),
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
  makeAdmin: (userId: string) => IFetch<User>({ method: 'POST', url: `${AUTH}/make-admin/`, data: { userId } }),

  // Reservation
  getReservation: (sectionId: string, reservationId: string) =>
    IFetch<Reservation>({ method: 'GET', url: `${SECTIONS}/${sectionId}/${RESERVATIONS}/${reservationId}/` }),
  getSectionReservations: (sectionId: string, filters?: any) =>
    IFetch<PaginationResponse<Reservation>>({ method: 'GET', url: `${SECTIONS}/${sectionId}/${RESERVATIONS}/`, data: filters || {} }),
  getGroupReservations: (groupId: string, filters?: any) =>
    IFetch<PaginationResponse<Reservation>>({ method: 'GET', url: `${GROUPS}/${groupId}/${RESERVATIONS}/`, data: filters || {} }),
  getUserReservations: (userId?: string, filters?: any) =>
    IFetch<PaginationResponse<Reservation>>({ method: 'GET', url: `${USERS}/${userId || ME}/${RESERVATIONS}/`, data: filters || {} }),
  createReservation: (sectionId: string, newRegistration: ReservationCreate) =>
    IFetch<Reservation>({ method: 'POST', url: `${SECTIONS}/${sectionId}/${RESERVATIONS}/`, data: newRegistration }),
  updateReservation: (sectionId: string, reservationId: string, updatedReservation: Partial<Reservation>) =>
    IFetch<Reservation>({ method: 'PUT', url: `${SECTIONS}/${sectionId}/${RESERVATIONS}/${reservationId}/`, data: updatedReservation }),
  deleteReservation: (sectionId: string, reservationId: string) =>
    IFetch<RequestResponse>({ method: 'DELETE', url: `${SECTIONS}/${sectionId}/${RESERVATIONS}/${reservationId}/` }),

  // Section
  getSection: (sectionId: string) => IFetch<Section>({ method: 'GET', url: `${SECTIONS}/${sectionId}/` }),
  getSectionStatistics: (sectionId: string, filters?: any) =>
    IFetch<Statistics>({ method: 'GET', url: `${SECTIONS}/${sectionId}/${STATISTICS}/`, data: filters || {} }),
  getSections: (filters?: any) => IFetch<PaginationResponse<SectionList>>({ method: 'GET', url: `${SECTIONS}/`, data: filters || {} }),
  createSection: (newSection: SectionCreate) => IFetch<Section>({ method: 'POST', url: `${SECTIONS}/`, data: newSection }),
  updateSection: (sectionId: string, updatedSection: Partial<Section>) =>
    IFetch<Section>({ method: 'PUT', url: `${SECTIONS}/${sectionId}/`, data: updatedSection }),
  deleteSection: (sectionId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${SECTIONS}/${sectionId}/` }),

  // Groups
  getGroup: (groupId: string) => IFetch<Group>({ method: 'GET', url: `${GROUPS}/${groupId}/` }),
  getGroups: (filters?: any) => IFetch<PaginationResponse<Group>>({ method: 'GET', url: `${GROUPS}/`, data: filters || {} }),
  createGroup: (newGroup: GroupCreate) => IFetch<Group>({ method: 'POST', url: `${GROUPS}/`, data: newGroup }),
  updateGroup: (groupId: string, updatedGroup: Partial<Group>) => IFetch<Group>({ method: 'PUT', url: `${GROUPS}/${groupId}/`, data: updatedGroup }),
  deleteGroup: (groupId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${GROUPS}/${groupId}/` }),

  // Memberships
  getMemberships: (groupId: string, filters?: any) =>
    IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${GROUPS}/${groupId}/${MEMBERSHIPS}/`, data: filters || {} }),
  createMembership: (groupId: string, email: string) =>
    IFetch<PaginationResponse<UserList>>({ method: 'POST', url: `${GROUPS}/${groupId}/${MEMBERSHIPS}/`, data: { email } }),
  deleteMembership: (groupId: string, userId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${GROUPS}/${groupId}/${MEMBERSHIPS}/${userId}/` }),
  batchAddMembership: (groupId: string, csvFile: File | Blob) => {
    const formData = new FormData();
    formData.append('file', csvFile);
    return IFetch<RequestResponse>({ method: 'POST', url: `${GROUPS}/${groupId}/${MEMBERSHIPS}/batch-memberships/`, formData });
  },

  // User
  getUser: (userId?: string) => IFetch<User>({ method: 'GET', url: `${USERS}/${userId || ME}/` }),
  getUserGroups: (userId?: string) => IFetch<Array<Group>>({ method: 'GET', url: `${USERS}/${userId || ME}/${GROUPS}/` }),
  getUsers: (filters?: any) => IFetch<PaginationResponse<UserList>>({ method: 'GET', url: `${USERS}/`, data: filters || {} }),
  updateUser: (userId: string, item: Partial<User>) => IFetch<User>({ method: 'PUT', url: `${USERS}/${userId}/`, data: item }),
  deleteUser: (userId: string) => IFetch<RequestResponse>({ method: 'DELETE', url: `${USERS}/${userId}/` }),
  batchAddUser: (csvFile: File | Blob) => {
    const formData = new FormData();
    formData.append('file', csvFile);
    return IFetch<RequestResponse>({ method: 'POST', url: `${USERS}/batch-users/`, formData });
  },

  // Upload file
  uploadFile: (file: File | Blob) => {
    const formData = new FormData();
    formData.append('image', file);
    return IFetch<FileUploadResponse>({
      method: 'POST',
      baseUrl: 'https://api.imgbb.com/1/',
      url: 'upload?key=909df01fa93bd63405c9a36d662523f3',
      formData,
      withAuth: false,
    });
  },
};

import { useMutation, useInfiniteQuery, useQuery, useQueryClient, UseMutationResult } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { Reservation, ReservationCreate, PaginationResponse, RequestResponse } from 'types/Types';
import { SECTION_QUERY_KEY, SECTION_ALL_QUERY_KEY, SECTION_STATISTICS_QUERY_KEY } from 'hooks/Section';
export const RESERVATION_QUERY_KEY = 'reservation';
export const USER_RESERVATIONS_QUERY_KEY = 'user_reservations';
export const SECTION_RESERVATIONS_QUERY_KEY = 'section_reservations';

/**
 * Get a specific reservation
 * @param sectionId - Id of section
 * @param reservationId - Id of reservation
 */
export const useReservationById = (sectionId: string, reservationId: string) => {
  return useQuery<Reservation, RequestResponse>([RESERVATION_QUERY_KEY, sectionId, reservationId], () => API.getReservation(sectionId, reservationId), {
    enabled: reservationId !== '' && sectionId !== '',
  });
};

/**
 * Get section reservations, paginated
 * @param sectionId - Id of section
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useSectionReservations = (sectionId: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<Reservation>, RequestResponse>(
    [RESERVATION_QUERY_KEY, 'section', sectionId, filters],
    ({ pageParam = 0 }) => API.getSectionReservations(sectionId, { sort: 'fromTime,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get group reservations, paginated
 * @param groupId - Id of group
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useGroupReservations = (groupId: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<Reservation>, RequestResponse>(
    [RESERVATION_QUERY_KEY, 'group', groupId, filters],
    ({ pageParam = 0 }) => API.getGroupReservations(groupId, { sort: 'fromTime,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Get user reservations, paginated
 * @param userId - Id of user
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useUserReservations = (userId?: string, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<Reservation>, RequestResponse>(
    [RESERVATION_QUERY_KEY, 'user', userId, filters],
    ({ pageParam = 0 }) => API.getUserReservations(userId, { sort: 'fromTime,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create a new reservation
 * @param sectionId - Id of section
 */
export const useCreateReservation = (sectionId: string): UseMutationResult<Reservation, RequestResponse, ReservationCreate, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((newReservation) => API.createReservation(sectionId, newReservation), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(RESERVATION_QUERY_KEY);
      queryClient.invalidateQueries([SECTION_QUERY_KEY, SECTION_ALL_QUERY_KEY]);
      queryClient.invalidateQueries([SECTION_QUERY_KEY, sectionId, SECTION_STATISTICS_QUERY_KEY]);
      queryClient.setQueryData([RESERVATION_QUERY_KEY, data.id], data);
    },
  });
};

/**
 * Update a reservation
 * @param sectionId - Id of section
 * @param reservationId - Id of reservation
 */
export const useUpdateReservation = (
  sectionId: string,
  reservationId: string,
): UseMutationResult<Reservation, RequestResponse, Partial<Reservation>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((updatedReservation) => API.updateReservation(sectionId, reservationId, updatedReservation), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(RESERVATION_QUERY_KEY);
      queryClient.setQueryData([RESERVATION_QUERY_KEY, reservationId], data);
    },
  });
};

/**
 * Delete a reservation
 * @param sectionId - Id of section
 * @param reservationId - Id of reservation
 */
export const useDeleteReservation = (sectionId: string, reservationId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deleteReservation(sectionId, reservationId), {
    onSuccess: () => {
      queryClient.invalidateQueries(RESERVATION_QUERY_KEY);
      queryClient.invalidateQueries([SECTION_QUERY_KEY, SECTION_ALL_QUERY_KEY]);
    },
  });
};

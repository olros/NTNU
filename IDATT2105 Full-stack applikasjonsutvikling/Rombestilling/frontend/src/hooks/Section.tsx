import { useMutation, useInfiniteQuery, useQuery, useQueryClient, UseMutationResult } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { SectionCreate, SectionList, Section, Statistics, PaginationResponse, RequestResponse } from 'types/Types';
export const SECTION_QUERY_KEY = 'section';
export const SECTION_ALL_QUERY_KEY = 'all_sections';
export const SECTION_STATISTICS_QUERY_KEY = 'statistics';

/**
 * Get a specific section
 * @param sectionId - Id of section
 */
export const useSectionById = (sectionId: string) => {
  return useQuery<Section, RequestResponse>([SECTION_QUERY_KEY, sectionId], () => API.getSection(sectionId), { enabled: sectionId !== '' });
};

/**
 * Get the sections, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useSections = (filters?: any) => {
  return useInfiniteQuery<PaginationResponse<SectionList>, RequestResponse>(
    [SECTION_QUERY_KEY, SECTION_ALL_QUERY_KEY, filters],
    ({ pageParam = 0 }) => API.getSections({ sort: 'name,ASC', ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create a new section
 */
export const useCreateSection = (): UseMutationResult<Section, RequestResponse, SectionCreate, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((newSection) => API.createSection(newSection), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(SECTION_QUERY_KEY);
      queryClient.setQueryData([SECTION_QUERY_KEY, data.id], data);
    },
  });
};

/**
 * Update a section
 * @param sectionId - Id of section
 */
export const useUpdateSection = (sectionId: string): UseMutationResult<Section, RequestResponse, Partial<Section>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((updatedSection) => API.updateSection(sectionId, updatedSection), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(SECTION_QUERY_KEY);
      queryClient.setQueryData([SECTION_QUERY_KEY, sectionId], data);
    },
  });
};

/**
 * Delete a section
 * @param sectionId - Id of section
 */
export const useDeleteSection = (sectionId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deleteSection(sectionId), {
    onSuccess: () => {
      queryClient.invalidateQueries(SECTION_QUERY_KEY);
    },
  });
};

// ------------------
// Section statistics
// ------------------

/**
 * Get statistics for a specific section
 * @param sectionId - Id of section
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useSectionStatisticsById = (sectionId: string, filters?: any) => {
  return useQuery<Statistics, RequestResponse>(
    [SECTION_QUERY_KEY, sectionId, SECTION_STATISTICS_QUERY_KEY, filters],
    () => API.getSectionStatistics(sectionId, filters),
    {
      enabled: sectionId !== '',
    },
  );
};

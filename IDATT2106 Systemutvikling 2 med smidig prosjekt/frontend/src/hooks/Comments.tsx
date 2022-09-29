import { useInfiniteQuery, useQueryClient, useMutation, UseMutationResult } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { PaginationResponse, RequestResponse, Comment } from 'types/Types';
import { updateFeedCache } from 'hooks/Feed';
export const COMMENTS_QUERY_KEY = 'comments';

export type CommentApp = 'activity' | 'post';

/**
 * Get all comments, paginated
 * @param filters - Filtering
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const useComments = (appId: string, app: CommentApp, enabled: boolean, filters?: any) => {
  return useInfiniteQuery<PaginationResponse<Comment>, RequestResponse>(
    [COMMENTS_QUERY_KEY, appId, app, filters],
    ({ pageParam = 0 }) => (app === 'activity' ? API.getActivityComments : API.getPostComments)(appId, { ...filters, page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
      enabled: enabled,
    },
  );
};

/**
 * Create a new comment
 */
export const useCreateComment = (appId: string, app: CommentApp): UseMutationResult<Comment, RequestResponse, Pick<Comment, 'comment'>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((comment) => (app === 'activity' ? API.createActivityComment : API.createPostComment)(appId, comment), {
    onSuccess: () => {
      queryClient.invalidateQueries([COMMENTS_QUERY_KEY, appId, app]);
      if (app === 'post') {
        updateFeedCache(queryClient, appId, (post) => ({ ...post, commentsCount: post.commentsCount + 1 }));
      }
    },
  });
};

/**
 * Delete a comment
 * @param commentId - Id of a comment
 */
export const useDeleteComment = (appId: string, commentId: string, app: CommentApp): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => (app === 'activity' ? API.deleteActivityComment : API.deletePostComment)(appId, commentId), {
    onSuccess: () => {
      queryClient.invalidateQueries([COMMENTS_QUERY_KEY, appId, app]);
      if (app === 'post') {
        updateFeedCache(queryClient, appId, (post) => ({ ...post, commentsCount: post.commentsCount - 1 }));
      }
    },
  });
};

/**
 * Edit a comment
 * @param commentId - Id of comment
 */
export const useEditComment = (
  appId: string,
  commentId: string,
  app: CommentApp,
): UseMutationResult<Comment, RequestResponse, Pick<Comment, 'comment'>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((comment) => (app === 'activity' ? API.editActivityComment : API.editPostComment)(appId, commentId, comment), {
    onSuccess: () => {
      queryClient.invalidateQueries([COMMENTS_QUERY_KEY, appId, app]);
    },
  });
};

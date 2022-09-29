import { useMutation, useInfiniteQuery, useQuery, useQueryClient, QueryClient, UseMutationResult, InfiniteData } from 'react-query';
import API from 'api/api';
import { getNextPaginationPage } from 'utils';
import { Like, Post, PostCreate, PaginationResponse, RequestResponse } from 'types/Types';
export const FEED_QUERY_KEY = 'feed';
export const FEED_ALL_QUERY_KEY = 'feed_all';
export const POST_QUERY_KEY = 'post';

/**
 * Get a specific post
 * @param postId - Id of post
 */
export const usePostById = (postId: string) => {
  return useQuery<Post, RequestResponse>([POST_QUERY_KEY, postId], () => API.getPost(postId), { enabled: postId !== '' });
};

/**
 * Get the feed with posts, paginated
 * @param filters - Filtering
 */
export const useFeed = (userId?: string) => {
  return useInfiniteQuery<PaginationResponse<Post>, RequestResponse>(
    [FEED_QUERY_KEY, userId || FEED_ALL_QUERY_KEY],
    ({ pageParam = 0 }) => API.getFeed({ ...(userId ? { 'creator.id': userId } : {}), sort: 'createdAt,DESC', page: pageParam }),
    {
      getNextPageParam: getNextPaginationPage,
    },
  );
};

/**
 * Create a new post
 */
export const useCreatePost = (): UseMutationResult<Post, RequestResponse, PostCreate, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((newPost: PostCreate) => API.createPost(newPost), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(FEED_QUERY_KEY);
      queryClient.setQueryData([POST_QUERY_KEY, data.id], data);
    },
  });
};

/**
 * Update an post
 * @param postId - Id of post
 */
export const useUpdatePost = (postId: string): UseMutationResult<Post, RequestResponse, Partial<Post>, unknown> => {
  const queryClient = useQueryClient();
  return useMutation((updatedPost: Partial<Post>) => API.updatePost(postId, updatedPost), {
    onSuccess: (data) => {
      queryClient.invalidateQueries(FEED_QUERY_KEY);
      queryClient.setQueryData([POST_QUERY_KEY, postId], data);
    },
  });
};

/**
 * Delete a post
 * @param postId - Id of post
 */
export const useDeletePost = (postId: string): UseMutationResult<RequestResponse, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deletePost(postId), {
    onSuccess: () => {
      queryClient.invalidateQueries(FEED_QUERY_KEY);
    },
  });
};

//////////////////////////////////
/////////// Post likes ///////////
//////////////////////////////////

/**
 * Like a post
 * @param postId - Id of post
 */
export const useCreatePostLike = (postId: string): UseMutationResult<Like, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.createPostLike(postId), {
    onSuccess: () => {
      queryClient.invalidateQueries([POST_QUERY_KEY, postId]);
      updateFeedCache(queryClient, postId, (post) => ({ ...post, hasLiked: true, likesCount: post.likesCount + 1 }));
    },
  });
};

/**
 * Remove a like to a post
 * @param postId - Id of post
 */
export const useRemovePostLike = (postId: string): UseMutationResult<Like, RequestResponse, unknown, unknown> => {
  const queryClient = useQueryClient();
  return useMutation(() => API.deletePostLike(postId), {
    onSuccess: () => {
      queryClient.invalidateQueries([POST_QUERY_KEY, postId]);
      updateFeedCache(queryClient, postId, (post) => ({ ...post, hasLiked: false, likesCount: post.likesCount - 1 }));
    },
  });
};

/**
 * Updates a given post in the feed cache
 * @param queryClient - QueryClient
 * @param postId - Id of post to update
 * @param updateFunc - Function which receives the post to update and returns the updated post
 */
export const updateFeedCache = (queryClient: QueryClient, postId: string, updateFunc: (post: Post) => Post) => {
  /* eslint-disable-next-line @typescript-eslint/ban-ts-comment */
  // @ts-ignore
  queryClient.setQueryData<InfiniteData<PaginationResponse<Post>>>([FEED_QUERY_KEY, FEED_ALL_QUERY_KEY], (data) => {
    if (!data) {
      return undefined;
    }
    const newPagesArray: Array<PaginationResponse<Post>> = [];
    data.pages.forEach((page) => {
      // const newData: Array<Post> = page.content.map((post) => (post.id === postId ? { ...post, hasLiked: false, likesCount: post.likesCount - 1 } : post));
      const newData: Array<Post> = page.content.map((post) => (post.id === postId ? updateFunc(post) : post));
      const newPage: PaginationResponse<Post> = { ...page, content: newData };
      newPagesArray.push(newPage);
    });
    return {
      pages: newPagesArray,
      pageParams: data.pageParams,
    };
  });
};

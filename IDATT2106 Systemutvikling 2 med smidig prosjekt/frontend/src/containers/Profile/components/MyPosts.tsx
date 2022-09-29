import { useMemo } from 'react';
import { useFeed } from 'hooks/Feed';

// Material UI
import { makeStyles } from '@material-ui/core/styles';

// Project components
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import Container from 'components/layout/Container';
import PostCard from 'containers/Feed/components/PostCard';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gap: theme.spacing(1),
    gridTemplateColumns: '1fr',
  },
  container: {
    padding: 0,
  },
}));

export type MyPostsProps = {
  userId?: string;
};

const MyPosts = ({ userId }: MyPostsProps) => {
  const classes = useStyles();
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useFeed(userId);
  const posts = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !posts.length && !isFetching, [posts, isFetching]);

  return (
    <Container className={classes.container} maxWidth='md'>
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen innlegg'} />}
        <div className={classes.list}>
          {posts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </div>
      </Pagination>
    </Container>
  );
};

export default MyPosts;

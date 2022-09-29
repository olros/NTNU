import { useMemo, useState } from 'react';
import Helmet from 'react-helmet';
import { useFeed } from 'hooks/Feed';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Button, Fab } from '@material-ui/core';

// Icons
import AddIcon from '@material-ui/icons/AddRounded';

// Project Components
import Navigation from 'components/navigation/Navigation';
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import PostCard from 'containers/Feed/components/PostCard';
import PostCreateCard from 'containers/Feed/components/PostCreateCard';

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  fab: {
    position: 'fixed',
    bottom: theme.spacing(2),
    right: theme.spacing(2),
    zIndex: 10,
  },
}));

const Feed = () => {
  const classes = useStyles();
  const [openCreate, setOpenCreate] = useState(false);
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useFeed();
  const posts = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !posts.length && !isFetching, [posts, isFetching]);

  return (
    <Navigation maxWidth='md' topbarVariant='dynamic'>
      <Helmet>
        <title>Feed - GIDD</title>
      </Helmet>
      <PostCreateCard onClose={() => setOpenCreate(false)} open={openCreate} />
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        <div className={classes.root}>
          <Button fullWidth onClick={() => setOpenCreate(true)}>
            Opprett innlegg
          </Button>
          {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen oppdateringer'} />}
          {posts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </div>
      </Pagination>
      <Fab aria-label='Meny' className={classes.fab} color='primary' onClick={() => setOpenCreate((prev) => !prev)} size='medium'>
        <AddIcon />
      </Fab>
    </Navigation>
  );
};

export default Feed;

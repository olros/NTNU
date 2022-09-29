import { useState, useMemo } from 'react';
import { useFollowers, useFollowing } from 'hooks/Followers';
import { useUser } from 'hooks/User';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import { List, Button, ButtonGroup } from '@material-ui/core';

// Project components
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import UserCard from 'containers/Users/components/UserCard';

const useStyles = makeStyles((theme) => ({
  buttons: {
    marginBottom: theme.spacing(1),
  },
  list: {
    display: 'grid',
    gap: theme.spacing(1),
    gridTemplateColumns: '1fr',
  },
}));

export type FollowProps = {
  userId?: string;
};

const Follow = ({ userId }: FollowProps) => {
  const classes = useStyles();
  const { data: user } = useUser(userId);
  const [view, setView] = useState<'following' | 'followers'>('followers');
  const useHook = view === 'following' ? useFollowing : useFollowers;
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useHook(userId);
  const users = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !users.length && !isFetching, [users, isFetching]);

  return (
    <>
      <ButtonGroup aria-label='Se følgere eller de du følger' className={classes.buttons} fullWidth>
        <Button onClick={() => setView('followers')} variant={view === 'followers' ? 'contained' : 'outlined'}>
          {`${user?.followerCount} følgere`}
        </Button>
        <Button onClick={() => setView('following')} variant={view === 'following' ? 'contained' : 'outlined'}>
          {`Følger ${user?.followingCount}`}
        </Button>
      </ButtonGroup>
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        {isEmpty && (
          <NotFoundIndicator
            header={
              error?.message ||
              (view === 'following' ? `${userId ? 'Brukeren' : 'Du'} følger ingen brukere` : `${userId ? 'Brukeren' : 'Du'} har ingen følgere`)
            }
          />
        )}
        <List className={classes.list}>
          {users.map((user) => (
            <UserCard isFollower={view === 'followers' && !userId} key={user.id} user={user} />
          ))}
        </List>
      </Pagination>
    </>
  );
};

export default Follow;

import { useMemo } from 'react';
import { UserList } from 'types/Types';
import { useMemberships, useDeleteMembership, useGroup } from 'hooks/Group';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser } from 'hooks/User';
import { startOfDay } from 'date-fns';
import { isUserAdmin } from 'utils';

// Material UI Components
import { makeStyles, Typography, Divider } from '@material-ui/core';

// Project Components
import VerifyDialog from 'components/layout/VerifyDialog';
import Pagination from 'components/layout/Pagination';
import AddMembership from 'components/miscellaneous/AddMembership';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import UserListItem, { UserListItemLoading } from 'containers/Users/components/UserListItem';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  paper: {
    overflow: 'hidden',
  },
  wrapper: {
    alignItems: 'center',
  },
  listContent: {
    padding: theme.spacing(1),
  },
}));

export type GroupMembershipsProps = {
  groupId: string;
};

export const GroupMemberships = ({ groupId }: GroupMembershipsProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { data: signedInUser } = useUser();
  const { data: group } = useGroup(groupId);
  const filters = useMemo(() => ({ fromTimeAfter: startOfDay(new Date()).toJSON() }), []);
  const { data, error, hasNextPage, fetchNextPage, isLoading, isFetching } = useMemberships(groupId, filters);
  const memberships = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !memberships.length && !isFetching, [memberships, isFetching]);
  const isAdmin = isUserAdmin(signedInUser) || Boolean(group?.creator.id === signedInUser?.id);

  const Membership = ({ user, creator }: { user: UserList; creator?: boolean }) => {
    const deleteMembership = useDeleteMembership(groupId);
    const remove = () =>
      deleteMembership.mutate(user.id, {
        onSuccess: (data) => {
          showSnackbar(data.message, 'success');
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    return (
      <UserListItem user={user}>
        {creator ? (
          <Typography variant='subtitle2'>Eier gruppen</Typography>
        ) : (
          isAdmin && (
            <VerifyDialog color='secondary' onConfirm={remove} titleText='Fjern fra gruppe?'>
              Fjern fra gruppe
            </VerifyDialog>
          )
        )}
      </UserListItem>
    );
  };

  return (
    <div className={classes.grid}>
      {group && <Membership creator user={group?.creator} />}
      <Divider />
      <AddMembership groupId={groupId}>Legg til bruker i gruppe</AddMembership>
      <Pagination fullWidth hasNextPage={hasNextPage} nextPage={() => fetchNextPage()}>
        <div className={classes.grid}>
          {isLoading && <UserListItemLoading />}
          {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen medlemmer'} />}
          {memberships.map((user) => (
            <Membership key={user.id} user={user} />
          ))}
        </div>
      </Pagination>
    </div>
  );
};

export default GroupMemberships;

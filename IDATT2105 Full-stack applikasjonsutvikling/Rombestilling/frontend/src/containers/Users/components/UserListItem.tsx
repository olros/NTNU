import { ReactNode } from 'react';
import classnames from 'classnames';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { useUser } from 'hooks/User';
import { UserList } from 'types/Types';
import { isUserAdmin } from 'utils';

// Material UI Components
import { makeStyles, Button, Typography, Skeleton } from '@material-ui/core';

// Icons
import ArrowIcon from '@material-ui/icons/ArrowForwardRounded';

// Project Components
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  paper: {
    padding: theme.spacing(2, 3),
  },
  top: {
    gridTemplateColumns: '1fr auto',
  },
  topText: {
    gap: 0,
  },
}));

export type UserListItemProps = {
  user: UserList;
  children?: ReactNode;
};

const UserListItem = ({ user, children }: UserListItemProps) => {
  const classes = useStyles();
  const { data } = useUser();
  return (
    <Paper className={classnames(classes.paper, classes.grid)}>
      <div className={classnames(classes.top, classes.grid)}>
        <div className={classnames(classes.topText, classes.grid)}>
          <Typography variant='h3'>{`${user.firstName} ${user.surname}`}</Typography>
          <Typography variant='caption'>{`${user.email} | ${user.phoneNumber}`}</Typography>
        </div>
        {(isUserAdmin(data) || user.id === data?.id) && (
          <Button component={Link} endIcon={<ArrowIcon />} to={`${URLS.USERS}${user.id}/`} variant='outlined'>
            Profil
          </Button>
        )}
      </div>
      {children}
    </Paper>
  );
};

export default UserListItem;

export const UserListItemLoading = () => {
  const classes = useStyles();
  return (
    <Paper className={classnames(classes.paper, classes.grid)}>
      <div className={classnames(classes.top, classes.grid)}>
        <div className={classnames(classes.topText, classes.grid)}>
          <Typography variant='h3'>
            <Skeleton width={130} />
          </Typography>
          <Typography variant='caption'>
            <Skeleton width={180} />
          </Typography>
        </div>
        <Skeleton height={50} width={100} />
      </div>
    </Paper>
  );
};

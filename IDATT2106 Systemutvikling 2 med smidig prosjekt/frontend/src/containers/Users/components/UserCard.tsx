import URLS from 'URLS';
import { Link } from 'react-router-dom';
import { UserList } from 'types/Types';
import { useUser } from 'hooks/User';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Avatar, List, ListItem, ListItemText, ListItemAvatar, ListItemSecondaryAction } from '@material-ui/core';

// Project components
import Paper from 'components/layout/Paper';
import FollowButton, { RemoveFollowButton } from 'containers/Profile/components/FollowButton';

const useStyles = makeStyles(() => ({
  secondaryText: {
    whiteSpace: 'break-spaces',
  },
}));

export type UserCardProps = {
  user: UserList;
  isFollower?: boolean;
};

const UserCard = ({ user, isFollower }: UserCardProps) => {
  const classes = useStyles();
  const { data } = useUser();
  return (
    <Paper noPadding>
      <List disablePadding>
        <ListItem button component={Link} to={`${URLS.USERS}${user.id}/`}>
          <ListItemAvatar>
            <Avatar src={user.image} />
          </ListItemAvatar>
          <ListItemText classes={{ secondary: classes.secondaryText }} primary={`${user.firstName} ${user.surname}`} secondary={`${user.email}`} />
          <ListItemSecondaryAction>
            {isFollower ? <RemoveFollowButton userId={user.id} /> : user.id !== data?.id && <FollowButton userId={user.id} />}
          </ListItemSecondaryAction>
        </ListItem>
      </List>
    </Paper>
  );
};

export default UserCard;

import { useState } from 'react';
import { Activity } from 'types/Types';

// Services
import { useCreateActivityLike, useRemoveActivityLike } from 'hooks/Activities';
import { useIsAuthenticated } from 'hooks/User';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { IconButton, Tooltip, IconButtonProps, Typography } from '@material-ui/core';

// Icons
import NotLikedIcon from '@material-ui/icons/FavoriteBorderRounded';
import LikedIcon from '@material-ui/icons/FavoriteRounded';
import ShareIcon from '@material-ui/icons/ShareRounded';

// Project components
import Paper from 'components/layout/Paper';
import PostCreateCard from 'containers/Feed/components/PostCreateCard';

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'grid',
    gridAutoFlow: 'column',
    gap: theme.spacing(0.5),
    alignItems: 'center',
    paddingLeft: theme.spacing(1),
  },
}));

export type ActivityLikeProps = IconButtonProps & {
  activity: Activity;
};

const ActivityLike = ({ activity }: ActivityLikeProps) => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();
  const createLike = useCreateActivityLike(activity.id);
  const deleteLike = useRemoveActivityLike(activity.id);
  const showSnackbar = useSnackbar();
  const [shareOpen, setShareOpen] = useState(false);

  const toggle = async () => {
    if (!isAuthenticated) {
      showSnackbar('Logg inn for å markere som favoritt', 'info');
    } else if (activity.hasLiked) {
      deleteLike.mutate(null, {
        onSuccess: () => {
          showSnackbar('Aktiviteten ble fjernet fra dine favoritter', 'info');
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    } else {
      createLike.mutate(null, {
        onSuccess: () => {
          showSnackbar('Aktiviteten ble lagt til dine favoritter', 'success');
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    }
  };
  return (
    <Paper className={classes.root} noPadding>
      <Typography>{activity.likesCount}</Typography>
      <Tooltip title={activity.hasLiked ? 'Fjern fra dine favoritter' : 'Legg til dine favoritter'}>
        <IconButton onClick={toggle}>{activity.hasLiked ? <LikedIcon aria-label='Slutt å like' /> : <NotLikedIcon aria-label='Lik dette' />}</IconButton>
      </Tooltip>
      <Tooltip title='Del med følgerne dine'>
        <IconButton aria-label='del' onClick={() => setShareOpen(true)}>
          <ShareIcon />
        </IconButton>
      </Tooltip>
      <PostCreateCard activity={activity} onClose={() => setShareOpen(false)} open={shareOpen} />
    </Paper>
  );
};

export default ActivityLike;

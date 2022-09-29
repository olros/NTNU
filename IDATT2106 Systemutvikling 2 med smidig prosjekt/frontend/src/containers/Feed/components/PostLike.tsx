import { Post } from 'types/Types';

// Services
import { useCreatePostLike, useRemovePostLike } from 'hooks/Feed';
import { useIsAuthenticated } from 'hooks/User';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Button, IconButtonProps } from '@material-ui/core';

// Icons
import NotLikedIcon from '@material-ui/icons/ThumbUpOutlined';
import LikedIcon from '@material-ui/icons/ThumbUpRounded';

const useStyles = makeStyles((theme) => ({
  like: {
    height: 39,
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
  },
}));

export type PostLikeProps = IconButtonProps & {
  post: Post;
};

const PostLike = ({ post }: PostLikeProps) => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();
  const createLike = useCreatePostLike(post.id);
  const deleteLike = useRemovePostLike(post.id);
  const showSnackbar = useSnackbar();

  const toggle = async () => {
    if (!isAuthenticated) {
      showSnackbar('Logg inn for å like dette innlegget', 'info');
    } else if (post.hasLiked) {
      deleteLike.mutate(null, {
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    } else {
      createLike.mutate(null, {
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    }
  };
  return (
    <Button aria-label='Lik innlegget' className={classes.like} endIcon={post.hasLiked ? <LikedIcon /> : <NotLikedIcon />} onClick={toggle} variant='text'>
      {post.likesCount}
    </Button>
  );
};

export default PostLike;

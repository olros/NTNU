import { useState } from 'react';
import classnames from 'classnames';
import { Link } from 'react-router-dom';
import { Post } from 'types/Types';
import { getTimeSince } from 'utils';
import { parseISO } from 'date-fns';
import URLS from 'URLS';
import { useUser } from 'hooks/User';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Avatar, List, ListItem, ListItemAvatar, ListItemText, ListItemSecondaryAction, IconButton } from '@material-ui/core';

// Icons
import MoreIcon from '@material-ui/icons/MoreVertRounded';

// Project Components
import Paper from 'components/layout/Paper';
import ActivityCard from 'components/layout/ActivityCard';
import AspectRatioImg from 'components/miscellaneous/AspectRatioImg';
import PostCreateCard from 'containers/Feed/components/PostCreateCard';
import PostLike from 'containers/Feed/components/PostLike';
import Comments from 'components/miscellaneous/Comments';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  paper: {
    padding: theme.spacing(1),
  },
  name: {
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
    textDecoration: 'none',
    '&:hover': {
      textDecoration: 'underline',
    },
  },
  creator: {
    padding: theme.spacing(0, 1),
  },
  img: {
    borderRadius: theme.shape.borderRadius,
  },
  menu: {
    right: 0,
  },
  reactions: {
    gridTemplateColumns: 'auto auto 1fr',
  },
}));

export type PostCardProps = {
  post: Post;
  preview?: boolean;
};

const PostCard = ({ post, preview = false }: PostCardProps) => {
  const classes = useStyles();
  const [openEdit, setOpenEdit] = useState(false);
  const { data: user } = useUser();

  const isAdmin = user?.id === post.creator.id;

  return (
    <Paper className={classnames(classes.grid, classes.paper)}>
      <List disablePadding>
        <ListItem className={classes.creator} component='div'>
          <ListItemAvatar>
            <Link to={`${URLS.USERS}${post.creator.id}/`}>
              <Avatar src={post.creator.image} />
            </Link>
          </ListItemAvatar>
          <ListItemText
            primary={<Link className={classes.name} to={`${URLS.USERS}${post.creator.id}/`}>{`${post.creator.firstName} ${post.creator.surname}`}</Link>}
            secondary={`${getTimeSince(parseISO(post.createdAt))}`}
          />
          {isAdmin && !preview && (
            <ListItemSecondaryAction className={classes.menu}>
              <PostCreateCard onClose={() => setOpenEdit(false)} open={openEdit} post={post} />
              <IconButton aria-label='Rediger post' onClick={() => setOpenEdit(true)}>
                <MoreIcon />
              </IconButton>
            </ListItemSecondaryAction>
          )}
        </ListItem>
      </List>
      {Boolean(post.content) && <Typography>{post.content}</Typography>}
      {post.image && <AspectRatioImg alt='Bilde' imgClassName={classes.img} src={post.image} />}
      {post.activity && <ActivityCard activity={post.activity} />}
      {!preview && (
        <Paper className={classnames(classes.grid, classes.reactions)} noPadding>
          <PostLike post={post} />
          <Comments commentsCount={post.commentsCount} id={post.id} isAdmin={isAdmin} type='post' />
        </Paper>
      )}
    </Paper>
  );
};

export default PostCard;

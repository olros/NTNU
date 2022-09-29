import { parseISO } from 'date-fns';
import { getTimeSince } from 'utils';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser } from 'hooks/User';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useDeleteComment, useEditComment, CommentApp } from 'hooks/Comments';
// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Avatar, IconButton, ListItem, ListItemAvatar, ListItemText, ListItemSecondaryAction, List } from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';

// Project Components
import Paper from 'components/layout/Paper';
import { Comment } from 'types/Types';
import VerifyDialog from 'components/layout/VerifyDialog';
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(0, 2, 2),
  },
  btnGroup: {
    right: 0,
  },
  text: {
    whiteSpace: 'break-spaces',
  },
}));

export type CommentProps = {
  comment: Comment;
  id: string;
  type: CommentApp;
  isAdmin?: boolean;
};

const CommentCard = ({ comment, isAdmin, id, type }: CommentProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { data: user } = useUser();
  const [open, setOpen] = useState(false);
  const { register, handleSubmit, formState } = useForm<FormValues>({
    defaultValues: comment,
  });
  const deleteComment = useDeleteComment(id, comment.id, type);
  const updateComment = useEditComment(id, comment.id, type);

  const editComment = async (data: FormValues) => {
    await updateComment.mutate(data, {
      onSuccess: () => {
        showSnackbar('Kommentaren ble oppdatert', 'success');
        setOpen(false);
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const remove = async () => {
    deleteComment.mutate(null, {
      onSuccess: (data) => {
        showSnackbar(data.message, 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  type FormValues = Pick<Comment, 'comment'>;

  return (
    <Paper className={classes.paper}>
      <List disablePadding>
        <ListItem disableGutters>
          <ListItemAvatar>
            <Avatar src={comment.user.image} />
          </ListItemAvatar>
          <ListItemText primary={`${comment.user.firstName} ${comment.user.surname}`} secondary={getTimeSince(parseISO(comment.createdAt))} />
          {(comment.user.id === user?.id || isAdmin) && (
            <ListItemSecondaryAction className={classes.btnGroup}>
              {comment.user.id === user?.id && (
                <>
                  <IconButton aria-label='rediger kommentar' onClick={() => setOpen(true)}>
                    <EditIcon />
                  </IconButton>
                  <Dialog onClose={() => setOpen(false)} open={open}>
                    <form onSubmit={handleSubmit(editComment)}>
                      <TextField formState={formState} fullWidth label='Tekst' multiline {...register('comment', { required: 'Feltet er påkrevd' })} required />
                      <SubmitButton formState={formState}>Oppdater kommentar</SubmitButton>
                    </form>
                  </Dialog>
                </>
              )}
              <VerifyDialog aria-label='slett kommentar' contentText='Hvis du sletter kommentaren vil den bli slettet' iconButton={true} onConfirm={remove}>
                <DeleteIcon />
              </VerifyDialog>
            </ListItemSecondaryAction>
          )}
        </ListItem>
      </List>
      <Typography className={classes.text}>{comment.comment}</Typography>
    </Paper>
  );
};

export default CommentCard;

import { useMemo } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { UserList } from 'types/Types';
import { useActivityInvitedUsers, useAddActivityInvitedUser, useRemoveActivityInvitedUser } from 'hooks/Activities';
import { useSnackbar } from 'hooks/Snackbar';
import { EMAIL_REGEX } from 'constant';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import { LinearProgress, Divider, Typography, List, ListItem, ListItemText, ListItemSecondaryAction } from '@material-ui/core';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project components
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';
import TextField from 'components/inputs/TextField';
import VerifyDialog from 'components/layout/VerifyDialog';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import Pagination from 'components/layout/Pagination';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gridGap: theme.spacing(1),
  },
  secondaryText: {
    whiteSpace: 'break-spaces',
  },
}));

export type ActivityInvitesProps = {
  activityId: string;
};

type FormValues = {
  email: string;
};

const ActivityInvites = ({ activityId }: ActivityInvitesProps) => {
  const classes = useStyles();
  const { data, isLoading, error, hasNextPage, fetchNextPage, isFetching } = useActivityInvitedUsers(activityId);
  const invites = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !invites.length && !isFetching, [invites, isFetching]);
  const inviteUser = useAddActivityInvitedUser(activityId);
  const removeActivityHost = useRemoveActivityInvitedUser(activityId);
  const showSnackbar = useSnackbar();
  const { handleSubmit, register, formState, reset } = useForm<FormValues>();

  const submit: SubmitHandler<FormValues> = async (data) => {
    inviteUser.mutate(data.email, {
      onSuccess: () => {
        showSnackbar('Personen ble invitert til å delta på denne aktiviteten', 'success');
        reset();
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const removeInvited = async (userId: string) => {
    removeActivityHost.mutate(userId, {
      onSuccess: () => {
        showSnackbar('Personen ble fjernet fra inviterte og kan nå verken se eller melde seg på aktiviteten', 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  type InvitedProps = {
    user: UserList;
  };
  const Invited = ({ user }: InvitedProps) => (
    <Paper noPadding>
      <ListItem>
        <ListItemText
          classes={{ secondary: classes.secondaryText }}
          primary={`${user.firstName} ${user.surname} ${user.id === user?.id ? '- (Deg)' : ''}`}
          secondary={`${user.email}`}
        />
        <ListItemSecondaryAction>
          <VerifyDialog iconButton onConfirm={() => removeInvited(user.id)} titleText='Fjern invitert bruker'>
            <DeleteIcon />
          </VerifyDialog>
        </ListItemSecondaryAction>
      </ListItem>
    </Paper>
  );

  if (isLoading) {
    return <LinearProgress />;
  }

  return (
    <div className={classes.list}>
      <Paper>
        <Typography variant='h3'>Inviter bruker</Typography>
        <Typography variant='subtitle2'>Brukeren vil motta en varsling på epost, samt kunne se og melde seg på aktiviteten</Typography>
        <form className={classes.list} onSubmit={handleSubmit(submit)}>
          <TextField
            formState={formState}
            label='Epost til bruker'
            {...register('email', {
              required: 'Feltet er påkrevd',
              pattern: {
                value: EMAIL_REGEX,
                message: 'Ugyldig e-post',
              },
            })}
            required
          />
          <SubmitButton disabled={isLoading} formState={formState}>
            Inviter bruker
          </SubmitButton>
        </form>
      </Paper>
      <Divider />
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen inviterte'} />}
        <List className={classes.list}>
          {!isEmpty && (
            <>
              <Typography variant='subtitle1'>Inviterte brukere:</Typography>
              {invites?.map((user) => (
                <Invited key={user.id} user={user} />
              ))}
            </>
          )}
        </List>
      </Pagination>
    </div>
  );
};

export default ActivityInvites;

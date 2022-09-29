import { useMemo } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { UserList } from 'types/Types';
import { useActivityHostsById, useActivityById, useAddActivityHost, useRemoveActivityHost } from 'hooks/Activities';
import { useUser } from 'hooks/User';
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

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gridGap: theme.spacing(1),
  },
  secondaryText: {
    whiteSpace: 'break-spaces',
  },
}));

export type ActivityHostsProps = {
  activityId: string;
};

type FormValues = {
  email: string;
};

const ActivityHosts = ({ activityId }: ActivityHostsProps) => {
  const classes = useStyles();
  const { data: user } = useUser();
  const { data: activity } = useActivityById(activityId);
  const { data: hosts, isLoading } = useActivityHostsById(activityId);
  const isCreator = useMemo(() => activity?.creator?.id === user?.id, [activity, user]);
  const addHost = useAddActivityHost(activityId);
  const removeActivityHost = useRemoveActivityHost(activityId);
  const showSnackbar = useSnackbar();
  const { handleSubmit, register, formState, reset } = useForm<FormValues>();

  const submit: SubmitHandler<FormValues> = async (data) => {
    addHost.mutate(data.email, {
      onSuccess: () => {
        showSnackbar('Arrangøren ble lagt til', 'success');
        reset();
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const removeHost = async (hostId: string) => {
    removeActivityHost.mutate(hostId, {
      onSuccess: () => {
        showSnackbar('Arrangøren ble fjernet', 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  type HostProps = {
    host: UserList;
  };
  const Host = ({ host }: HostProps) => (
    <Paper noPadding>
      <ListItem>
        <ListItemText
          classes={{ secondary: classes.secondaryText }}
          primary={`${host.firstName} ${host.surname} ${host.id === user?.id ? '- (Deg)' : ''}`}
          secondary={`${host.email}`}
        />
        {isCreator && host.id !== user?.id && (
          <ListItemSecondaryAction>
            <VerifyDialog iconButton onConfirm={() => removeHost(host.id)} titleText='Fjern arrangør'>
              <DeleteIcon />
            </VerifyDialog>
          </ListItemSecondaryAction>
        )}
      </ListItem>
    </Paper>
  );

  if (isLoading) {
    return <LinearProgress />;
  }

  return (
    <List className={classes.list}>
      <Typography variant='subtitle1'>Opprettet av:</Typography>
      {activity !== undefined && activity?.creator !== null && <Host host={activity.creator} />}
      <Divider />
      {Boolean(hosts?.length) && (
        <>
          <Typography variant='subtitle1'>Andre arrangører:</Typography>
          {hosts?.map((host) => (
            <Host host={host} key={host.id} />
          ))}
          <Divider />
        </>
      )}
      <Paper>
        <Typography variant='h3'>Legg til arrangør</Typography>
        <form className={classes.list} onSubmit={handleSubmit(submit)}>
          <TextField
            formState={formState}
            label='Epost til ny arrangør'
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
            Legg til arrangør
          </SubmitButton>
        </form>
      </Paper>
    </List>
  );
};

export default ActivityHosts;

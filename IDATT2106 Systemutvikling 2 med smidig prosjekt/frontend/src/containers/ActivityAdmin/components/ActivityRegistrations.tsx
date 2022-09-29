import { useMemo } from 'react';
import { Registration } from 'types/Types';
import { useActivityRegistrations, useDeleteActivityRegistration } from 'hooks/Activities';
import { useUser } from 'hooks/User';
import { useSnackbar } from 'hooks/Snackbar';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import { LinearProgress, Typography, List, ListItem, ListItemText, ListItemSecondaryAction } from '@material-ui/core';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project components
import Paper from 'components/layout/Paper';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import Pagination from 'components/layout/Pagination';
import VerifyDialog from 'components/layout/VerifyDialog';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gridGap: theme.spacing(1),
  },
  paper: {
    background: theme.palette.background.default,
  },
  secondaryText: {
    whiteSpace: 'break-spaces',
  },
}));

export type ActivityRegistrationsProps = {
  activityId: string;
};

const ActivityRegistrations = ({ activityId }: ActivityRegistrationsProps) => {
  const classes = useStyles();
  const { data: user } = useUser();
  const { data, isLoading, error, hasNextPage, fetchNextPage, isFetching } = useActivityRegistrations(activityId);
  const registrations = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !registrations.length && !isFetching, [registrations, isFetching]);
  const deleteRegistration = useDeleteActivityRegistration(activityId);
  const showSnackbar = useSnackbar();

  const removeRegistration = async (hostId: string) => {
    deleteRegistration.mutate(hostId, {
      onSuccess: () => {
        showSnackbar('Deltageren ble fjernet', 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  type RegistrationProps = {
    registration: Registration;
  };
  const Registration = ({ registration }: RegistrationProps) => (
    <Paper className={classes.paper} noPadding>
      <ListItem>
        <ListItemText
          classes={{ secondary: classes.secondaryText }}
          primary={`${registration.user.firstName} ${registration.user.surname} ${registration.user.id === user?.id ? '- (Deg)' : ''}`}
          secondary={`${registration.user.email}`}
        />
        <ListItemSecondaryAction>
          <VerifyDialog iconButton onConfirm={() => removeRegistration(registration.user.id)} titleText='Fjern deltager'>
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
    <List className={classes.list}>
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen påmeldte'} />}
        {!isEmpty && (
          <>
            <Typography variant='subtitle1'>Påmeldte:</Typography>
            {registrations?.map((registration) => (
              <Registration key={registration.user.id} registration={registration} />
            ))}
          </>
        )}
      </Pagination>
    </List>
  );
};

export default ActivityRegistrations;

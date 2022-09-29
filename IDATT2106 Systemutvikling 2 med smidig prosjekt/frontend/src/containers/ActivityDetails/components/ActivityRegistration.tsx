import { useState } from 'react';
import { User, Activity } from 'types/Types';
import { useCreateActivityRegistration } from 'hooks/Activities';
import { useSnackbar } from 'hooks/Snackbar';
import { formatDate } from 'utils';
import { parseISO } from 'date-fns';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

// Project components
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  icon: {
    marginRight: theme.spacing(1),
    color: theme.palette.text.secondary,
  },
  listItem: {
    display: 'flex',
    flexDirection: 'row',
  },
  button: {
    height: 50,
    fontWeight: 'bold',
  },
}));

export type ActivityRegistrationProps = {
  activity: Activity;
  user: User;
  closeDialog: () => void;
};

const ActivityRegistration = ({ activity, user, closeDialog }: ActivityRegistrationProps) => {
  const classes = useStyles();
  const createRegistration = useCreateActivityRegistration(activity.id);
  const showSnackbar = useSnackbar();
  const [isLoading, setIsLoading] = useState(false);

  const signUp = async () => {
    setIsLoading(true);
    createRegistration.mutate(user.id, {
      onSuccess: () => {
        showSnackbar('Påmeldingen var vellykket', 'success');
        closeDialog();
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
      onSettled: () => {
        setIsLoading(false);
      },
    });
  };

  return (
    <>
      <div className={classes.list}>
        <Typography align='center' variant='h2'>
          Meld deg på
        </Typography>
        <Paper>
          <Typography>{`Fra: ${formatDate(parseISO(activity.startDate))}`}</Typography>
          <Typography>{`Til: ${formatDate(parseISO(activity.endDate))}`}</Typography>
        </Paper>
        <Button className={classes.button} disabled={isLoading} fullWidth onClick={signUp}>
          Meld deg på
        </Button>
      </div>
    </>
  );
};

export default ActivityRegistration;

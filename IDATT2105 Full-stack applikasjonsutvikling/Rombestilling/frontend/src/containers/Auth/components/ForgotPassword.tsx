import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import Helmet from 'react-helmet';
import URLS from 'URLS';
import { useForgotPassword } from 'hooks/User';
import { useSnackbar } from 'hooks/Snackbar';
import { EMAIL_REGEX } from 'constant';

// Material UI Components
import { makeStyles, Typography, Button } from '@material-ui/core';

// Project Components
import SubmitButton from 'components/inputs/SubmitButton';
import TextField from 'components/inputs/TextField';

const useStyles = makeStyles((theme) => ({
  grid: {
    margin: theme.spacing(1, 'auto'),
    display: 'grid',
  },
  button: {
    marginTop: theme.spacing(2),
  },
}));

type ForgotPasswordData = {
  email: string;
};

const ForgotPassword = () => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const forgotPassword = useForgotPassword();
  const { register, formState, handleSubmit, setError, reset } = useForm<ForgotPasswordData>();

  const onSubmit = async (data: ForgotPasswordData) => {
    forgotPassword.mutate(data.email, {
      onSuccess: () => {
        showSnackbar(
          'Hvis eposten tilhører en registrert brukerkonto, så vil du straks motta en link på epost der du kan tilbakestille passordet ditt.',
          'success',
        );
        reset();
      },
      onError: (e) => {
        setError('email', { message: e.message || 'Noe gikk galt' });
      },
    });
  };

  return (
    <>
      <Helmet>
        <title>Glemt passord</title>
      </Helmet>
      <form className={classes.grid} onSubmit={handleSubmit(onSubmit)}>
        <Typography variant='h2'>Glemt passord</Typography>
        <Typography variant='subtitle2'>Skriv inn eposten din, så vil vi sende deg en epost der du kan tilbakestille passordet ditt.</Typography>
        <TextField
          disabled={forgotPassword.isLoading}
          formState={formState}
          label='Epost'
          {...register('email', {
            required: 'Feltet er påkrevd',
            pattern: {
              value: EMAIL_REGEX,
              message: 'Ugyldig e-post',
            },
          })}
          required
          type='email'
        />
        <SubmitButton className={classes.button} disabled={forgotPassword.isLoading} formState={formState}>
          Send tilbakestill passord-epost
        </SubmitButton>
      </form>
      <Button className={classes.grid} color='secondary' component={Link} disabled={forgotPassword.isLoading} fullWidth to={URLS.LOGIN} variant='outlined'>
        Logg inn
      </Button>
    </>
  );
};

export default ForgotPassword;

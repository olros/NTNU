import { useForm } from 'react-hook-form';
import { Link, useParams, useNavigate } from 'react-router-dom';
import Helmet from 'react-helmet';
import URLS from 'URLS';
import { useResetPassword } from 'hooks/User';
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

type ResetPasswordData = {
  email: string;
  password: string;
};

const ResetPassword = () => {
  const classes = useStyles();
  const { token } = useParams();
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const resetPassword = useResetPassword();
  const { register, formState, handleSubmit, setError } = useForm<ResetPasswordData>();

  const onSubmit = async (data: ResetPasswordData) => {
    resetPassword.mutate(
      { email: data.email, password: data.password, token },
      {
        onSuccess: () => {
          showSnackbar('Passordet ble oppdatert', 'success');
          navigate(URLS.LOGIN);
        },
        onError: (e) => {
          setError('email', { message: e.message || 'Noe gikk galt' });
        },
      },
    );
  };

  return (
    <>
      <Helmet>
        <title>Tilbakestill passord</title>
      </Helmet>
      <form className={classes.grid} onSubmit={handleSubmit(onSubmit)}>
        <Typography variant='h2'>Tilbakestill passord</Typography>
        <TextField
          disabled={resetPassword.isLoading}
          formState={formState}
          label='Din epost'
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
        <TextField
          disabled={resetPassword.isLoading}
          formState={formState}
          label='Nytt passord'
          {...register('password', {
            required: 'Feltet er påkrevd',
          })}
          required
          type='password'
        />
        <SubmitButton className={classes.button} disabled={resetPassword.isLoading} formState={formState}>
          Tilbakestill passord
        </SubmitButton>
      </form>
      <Button className={classes.grid} color='secondary' component={Link} disabled={resetPassword.isLoading} fullWidth to={URLS.LOGIN} variant='outlined'>
        Logg inn
      </Button>
    </>
  );
};

export default ResetPassword;

import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import Helmet from 'react-helmet';
import URLS from 'URLS';
import { useLogin } from 'hooks/User';
import { EMAIL_REGEX } from 'constant';

// Material UI Components
import { makeStyles, Typography, Button } from '@material-ui/core';

// Project Components
import SubmitButton from 'components/inputs/SubmitButton';
import TextField from 'components/inputs/TextField';

const useStyles = makeStyles((theme) => ({
  button: {
    margin: theme.spacing(1, 'auto'),
  },
}));

type LoginData = {
  email: string;
  password: string;
};

const LogIn = () => {
  const classes = useStyles();
  const navigate = useNavigate();
  const logIn = useLogin();
  const { register, formState, handleSubmit, setError } = useForm<LoginData>();

  const onLogin = async (data: LoginData) => {
    logIn.mutate(
      { email: data.email, password: data.password },
      {
        onSuccess: () => {
          navigate(URLS.LANDING);
        },
        onError: (e) => {
          setError('password', { message: e.message || 'Noe gikk galt' });
        },
      },
    );
  };

  return (
    <>
      <Helmet>
        <title>Logg inn</title>
      </Helmet>
      <form onSubmit={handleSubmit(onLogin)}>
        <Typography variant='h2'>Logg inn</Typography>
        <TextField
          disabled={logIn.isLoading}
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
        <TextField
          disabled={logIn.isLoading}
          formState={formState}
          label='Passord'
          {...register('password', { required: 'Feltet er påkrevd' })}
          required
          type='password'
        />
        <SubmitButton className={classes.button} disabled={logIn.isLoading} formState={formState}>
          Logg inn
        </SubmitButton>
      </form>
      <Button className={classes.button} color='secondary' component={Link} disabled={logIn.isLoading} fullWidth to={URLS.FORGOT_PASSWORD} variant='outlined'>
        Glemt passord?
      </Button>
    </>
  );
};

export default LogIn;

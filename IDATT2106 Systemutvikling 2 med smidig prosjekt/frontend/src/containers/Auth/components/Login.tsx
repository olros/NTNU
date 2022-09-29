import { useForm } from 'react-hook-form';
import classnames from 'classnames';
import { Link, useNavigate } from 'react-router-dom';
import Helmet from 'react-helmet';
import URLS from 'URLS';
import { useLogin } from 'hooks/User';
import { EMAIL_REGEX } from 'constant';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

// Project Components
import SubmitButton from 'components/inputs/SubmitButton';
import TextField from 'components/inputs/TextField';

const useStyles = makeStyles((theme) => ({
  grid: {
    margin: theme.spacing(1, 'auto'),
    display: 'grid',
  },
  buttons: {
    gap: theme.spacing(1),
    gridTemplateColumns: '1fr 1fr',
  },
  button: {
    marginTop: theme.spacing(2),
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
      <form className={classes.grid} onSubmit={handleSubmit(onLogin)}>
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
      <div className={classnames(classes.grid, classes.buttons)}>
        <Button color='secondary' component={Link} disabled={logIn.isLoading} fullWidth to={URLS.SIGNUP} variant='outlined'>
          Opprett bruker
        </Button>
        <Button color='secondary' component={Link} disabled={logIn.isLoading} fullWidth to={URLS.FORGOT_PASSWORD} variant='outlined'>
          Glemt passord?
        </Button>
      </div>
    </>
  );
};

export default LogIn;

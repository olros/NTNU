import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import Helmet from 'react-helmet';
import URLS from 'URLS';
import { useCreateUser } from 'hooks/User';
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

type SignUpData = {
  firstName: string;
  surname: string;
  email: string;
  password: string;
  repeatPassword: string;
};

const SignUp = () => {
  const classes = useStyles();
  const navigate = useNavigate();
  const createUser = useCreateUser();
  const { register, getValues, formState, handleSubmit, setError } = useForm<SignUpData>();

  const onSignup = async (data: SignUpData) => {
    createUser.mutate(
      { firstName: data.firstName, surname: data.surname, email: data.email, password: data.password },
      {
        onSuccess: () => {
          navigate(URLS.LOGIN);
        },
        onError: (e) => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          setError('password', { message: e.data.password.replace(/,/g, ' ') || 'Noe gikk galt' });
        },
      },
    );
  };

  return (
    <>
      <Helmet>
        <title>Opprett bruker</title>
      </Helmet>
      <form className={classes.grid} onSubmit={handleSubmit(onSignup)}>
        <Typography variant='h2'>Opprett bruker</Typography>
        <TextField
          disabled={createUser.isLoading}
          formState={formState}
          label='Fornavn'
          {...register('firstName', { required: 'Feltet er påkrevd' })}
          required
        />
        <TextField
          disabled={createUser.isLoading}
          formState={formState}
          label='Etternavn'
          {...register('surname', { required: 'Feltet er påkrevd' })}
          required
        />
        <TextField
          disabled={createUser.isLoading}
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
          disabled={createUser.isLoading}
          formState={formState}
          label='Passord'
          {...register('password', { required: 'Feltet er påkrevd' })}
          required
          type='password'
        />
        <TextField
          disabled={createUser.isLoading}
          formState={formState}
          label='Gjennta passord'
          {...register('repeatPassword', {
            required: 'Feltet er påkrevd',
            validate: {
              passordEqual: (value) => value === getValues().password || 'Passordene er ikke like',
            },
          })}
          required
          type='password'
        />
        <SubmitButton className={classes.button} disabled={createUser.isLoading} formState={formState}>
          Opprett bruker
        </SubmitButton>
      </form>
      <Button className={classes.grid} color='secondary' component={Link} disabled={createUser.isLoading} fullWidth to={URLS.LOGIN} variant='outlined'>
        Logg inn
      </Button>
    </>
  );
};

export default SignUp;

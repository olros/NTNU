import Helmet from 'react-helmet';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { useIsAuthenticated } from 'hooks/User';

// Material UI Components
import { makeStyles, Button, Typography } from '@material-ui/core';

// Project Components
import Logo from 'components/miscellaneous/Logo';
import Container from 'components/layout/Container';

const useStyles = makeStyles((theme) => ({
  wrapper: {
    display: 'flex',
    flexDirection: 'column',
  },
  img: {
    width: '100%',
    maxHeight: '70vh',
    objectFit: 'contain',
  },
  imgPadding: {
    paddingTop: theme.spacing(3),
  },
  buttons: {
    margin: theme.spacing(2, 'auto'),
    display: 'grid',
    gap: theme.spacing(1),
    maxWidth: 200,
  },
  logo: {
    minWidth: '250px',
    width: '46%',
    maxWidth: '100%',
    height: 'auto',
    margin: theme.spacing(5, 'auto'),
    [theme.breakpoints.down('md')]: {
      minWidth: '200px',
    },
  },
  logoWrapper: {
    display: 'flex',
    margin: 'auto',
    marginTop: theme.spacing(2),
    maxWidth: 200,
    maxHeight: 200,
    marginBottom: theme.spacing(2),
  },
}));

const Http404 = () => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();

  return (
    <Container>
      <Helmet>
        <title>404</title>
      </Helmet>
      <div className={classes.wrapper}>
        <div className={classes.logoWrapper}>
          <Logo />
        </div>
        <Typography align='center' variant='h1'>
          {isAuthenticated ? 'Du er innlogget, men vi kunne fremdeles ikke finne siden :(' : 'Kunne ikke finne siden'}
        </Typography>
        <div className={classes.buttons}>
          <Button color='primary' component={Link} to={URLS.LANDING}>
            Til forsiden
          </Button>
          {!isAuthenticated && (
            <Button color='primary' component={Link} to={URLS.LOGIN} variant='outlined'>
              Logg inn
            </Button>
          )}
        </div>
      </div>
    </Container>
  );
};

export default Http404;

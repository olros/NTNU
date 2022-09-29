import Helmet from 'react-helmet';
import { Link, Navigate } from 'react-router-dom';
import URLS from 'URLS';
import { useIsAuthenticated } from 'hooks/User';

// Material UI Components
import { makeStyles, Typography, Button } from '@material-ui/core';

// Project Components
import Container from 'components/layout/Container';
import Logo from 'components/miscellaneous/Logo';

const useStyles = makeStyles((theme) => ({
  cover: {
    position: 'relative',
    minHeight: '90vh',
    width: '100%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
  },
  activityContainer: {
    textAlign: 'center',
    paddingTop: theme.spacing(2),
  },
  button: {
    marginTop: theme.spacing(2),
    color: theme.palette.common.white,
    borderColor: theme.palette.common.white,
  },
  logoWrapper: {
    display: 'flex',
    margin: 'auto',
    marginTop: theme.spacing(2),
    maxWidth: 200,
    maxHeight: 200,
    marginBottom: theme.spacing(2),
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
}));

const Landing = () => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();

  return (
    <Container>
      <Helmet>
        <title>Forsiden - Rombestilling</title>
      </Helmet>
      <div className={classes.cover}>
        <div className={classes.logoWrapper}>
          <Logo />
        </div>
        <Typography align='center' color='inherit' variant='h1'>
          Rombestilling
        </Typography>
        <Typography align='center' color='inherit' variant='h3'>
          Reserver et rom nå!
        </Typography>
        {isAuthenticated ? (
          <Navigate replace to={URLS.PROFILE} />
        ) : (
          <Button className={classes.button} component={Link} to={URLS.LOGIN} variant='outlined'>
            Logg inn
          </Button>
        )}
      </div>
    </Container>
  );
};

export default Landing;

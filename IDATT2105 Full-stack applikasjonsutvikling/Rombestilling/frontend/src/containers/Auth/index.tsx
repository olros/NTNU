import { Link, Routes, Route, Outlet } from 'react-router-dom';
import { AUTH_RELATIVE_ROUTES } from 'URLS';
import Helmet from 'react-helmet';
import URLS from 'URLS';

// Material UI Components
import { makeStyles, Button } from '@material-ui/core';

// Project Components
import Logo from 'components/miscellaneous/Logo';
import Login from 'containers/Auth/components/Login';
import ForgotPassword from 'containers/Auth/components/ForgotPassword';
import ResetPassword from 'containers/Auth/components/ResetPassword';

// Images
import ART_IMG from 'assets/img/art.jpg';

const useStyles = makeStyles((theme) => ({
  wrapper: {
    minHeight: '100vh',
    display: 'grid',
    gridTemplateColumns: '600px 1fr',
    [theme.breakpoints.down('xl')]: {
      gridTemplateColumns: '500px 1fr',
    },
    [theme.breakpoints.down('lg')]: {
      gridTemplateColumns: '1fr',
    },
  },
  image: {
    [theme.breakpoints.up('lg')]: {
      background: `url('${ART_IMG}')`,
      backgroundPosition: 'center bottom',
      backgroundSize: 'cover',
    },
    [theme.breakpoints.down('lg')]: {
      position: 'absolute',
    },
  },
  root: {
    maxWidth: theme.breakpoints.values.sm,
    width: '100%',
    margin: theme.spacing(4, 'auto'),
    padding: theme.spacing(1),
  },
  logoContainer: {
    height: 50,
    marginBottom: theme.spacing(5),
  },
  logo: {
    width: 'auto',
    margin: 0,
  },
}));
const Auth = () => {
  const classes = useStyles();
  return (
    <div className={classes.wrapper}>
      <Helmet>
        <title>Logg inn</title>
      </Helmet>
      <div className={classes.image} />
      <div className={classes.root}>
        <div className={classes.logoContainer}>
          <Logo className={classes.logo} />
        </div>
        <Routes>
          <Route element={<ResetPassword />} path={`${AUTH_RELATIVE_ROUTES.RESET_PASSWORD}:token/`} />
          <Route element={<ForgotPassword />} path={AUTH_RELATIVE_ROUTES.FORGOT_PASSWORD} />
          <Route element={<Login />} path='*' />
        </Routes>
        <Outlet />
        <Button color='primary' component={Link} fullWidth to={URLS.LANDING} variant='text'>
          Til forsiden
        </Button>
      </div>
    </div>
  );
};

export default Auth;

import { useMemo } from 'react';
import Helmet from 'react-helmet';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { useIsAuthenticated } from 'hooks/User';
import { useActivities } from 'hooks/Activities';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Button } from '@material-ui/core';

// Project Components
import Navigation from 'components/navigation/Navigation';
import image from 'assets/img/DefaultBackground.jpg';
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import ActivityCard from 'components/layout/ActivityCard';
import Container from 'components/layout/Container';
import MasonryGrid from 'components/layout/MasonryGrid';
import Feed from 'containers/Feed';
import LogoIcon from 'components/miscellaneous/LogoIcon';

const useStyles = makeStyles((theme) => ({
  header: {
    marginTop: theme.spacing(1),
  },
  cover: {
    position: 'relative',
    color: theme.palette.common.white,
    height: '100vh',
    width: '100%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
  },
  img: {
    background: `${theme.palette.colors.gradient}, url(${image}) center center/cover no-repeat scroll`,
    objectFit: 'contain',
    boxShadow: 'inset 0 0 0 1000px rgba(0, 0, 0, 0.2)',
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: 0,
    right: 0,
    filter: 'blur(1px)',
    zIndex: -1,
  },
  activityContainer: {
    textAlign: 'center',
    paddingTop: theme.spacing(2),
  },
  btnGroup: {
    display: 'grid',
    gap: theme.spacing(1),
    paddingTop: theme.spacing(1),
    gridTemplateColumns: 'auto auto',
  },
  button: {
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
  const isAuthenticated = useIsAuthenticated();
  if (isAuthenticated) {
    return <Feed />;
  }
  return <NotAuthedLanding />;
};

const NotAuthedLanding = () => {
  const classes = useStyles();
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useActivities();
  const activities = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !activities.length && !isFetching, [activities, isFetching]);

  return (
    <Navigation maxWidth={false} topbarVariant={'transparent'}>
      <Helmet>
        <title>Forsiden - GIDD</title>
      </Helmet>
      <div className={classes.cover}>
        <div className={classes.img} />
        <div className={classes.logoWrapper}>
          <LogoIcon />
        </div>
        <Typography align='center' color='inherit' variant='h1'>
          GIDD
        </Typography>
        <Typography align='center' color='inherit' variant='h3'>
          Det er bare å gidde.
        </Typography>
        <div className={classes.btnGroup}>
          <Button className={classes.button} component={Link} to={URLS.LOGIN} variant='outlined'>
            Logg inn
          </Button>
          <Button className={classes.button} component={Link} to={URLS.SIGNUP} variant='outlined'>
            Registrer deg
          </Button>
        </div>
      </div>
      <Container className={classes.activityContainer}>
        <Typography gutterBottom variant='h1'>
          Nye aktiviteter
        </Typography>
        <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
          <MasonryGrid>
            {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen aktiviteter'} />}
            {activities.map((activity) => (
              <ActivityCard activity={activity} gutterBottom key={activity.id} />
            ))}
          </MasonryGrid>
        </Pagination>
      </Container>
    </Navigation>
  );
};

export default Landing;

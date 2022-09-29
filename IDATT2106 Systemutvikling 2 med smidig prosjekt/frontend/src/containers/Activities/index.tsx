import { useState, useMemo } from 'react';
import Helmet from 'react-helmet';
import classnames from 'classnames';
import { useActivities } from 'hooks/Activities';
import URLS from 'URLS';
import { Link } from 'react-router-dom';
import { useIsAuthenticated } from 'hooks/User';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Button, ButtonGroup, Collapse, Hidden } from '@material-ui/core';

// Project Components
import Navigation from 'components/navigation/Navigation';
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import Calendar from 'components/miscellaneous/Calendar';
import ActivityCard from 'components/layout/ActivityCard';
import MasonryGrid from 'components/layout/MasonryGrid';
import ActivitiesSearch from 'containers/Activities/components/ActivitiesSearch';
import ActivitiesMap from 'components/miscellaneous/ActivitiesMap';

const useStyles = makeStyles((theme) => ({
  top: {
    width: '100%',
    padding: theme.spacing(4, 2),
  },
  grid: {
    display: 'grid',
    gap: theme.spacing(2),
    alignItems: 'self-start',
    [theme.breakpoints.down('md')]: {
      gap: theme.spacing(1),
    },
  },
  root: {
    gridTemplateColumns: '300px 1fr',
    [theme.breakpoints.down('xl')]: {
      gridTemplateColumns: '1fr',
    },
  },
  searchWrapper: {
    margin: 'auto',
  },
  title: {
    color: theme.palette.getContrastText(theme.palette.colors.topbar),
    paddingBottom: theme.spacing(2),
  },
  subtitle: {
    margin: 'auto 0',
  },
  row: {
    display: 'flex',
    justifyContent: 'flex-end',
  },
  filterRow: {
    gridTemplateColumns: '1fr auto',
  },
}));

export type ActivityFilters = {
  search?: string;
  ['trainingLevel.level']?: string;
  startDateAfter?: string;
  startDateBefore?: string;
  range?: number;
  lat?: number;
  lng?: number;
  sort?: string;
};

const Activities = () => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();
  const [filters, setFilters] = useState<ActivityFilters>({});
  const [view, setView] = useState<'list' | 'calendar' | 'map'>('list');
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useActivities(filters);
  const activities = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !activities.length && !isFetching, [activities, isFetching]);

  return (
    <Navigation topbarVariant='dynamic'>
      <Helmet>
        <title>Aktiviteter</title>
      </Helmet>
      <div className={classes.top}>
        <div className={classes.searchWrapper}>
          <Typography align='center' className={classes.title} variant='h1'>
            Aktiviteter
          </Typography>
        </div>
      </div>
      <div className={classnames(classes.grid, classes.root)}>
        <div className={classes.grid}>
          <div className={classnames(classes.grid, classes.filterRow)}>
            <Typography align='left' className={classes.subtitle} variant='h2'>
              Filtrer
            </Typography>
            <Hidden xlUp>
              {isAuthenticated && (
                <Button color='primary' component={Link} to={URLS.ADMIN_ACTIVITIES} variant='text'>
                  Opprett aktivitet
                </Button>
              )}
            </Hidden>
          </div>
          <ButtonGroup aria-label='Set calendar or list' fullWidth>
            <Button onClick={() => setView('list')} variant={view === 'list' ? 'contained' : 'outlined'}>
              Liste
            </Button>
            <Button onClick={() => setView('calendar')} variant={view === 'calendar' ? 'contained' : 'outlined'}>
              Kalender
            </Button>
            <Button onClick={() => setView('map')} variant={view === 'map' ? 'contained' : 'outlined'}>
              Kart
            </Button>
          </ButtonGroup>
          <ActivitiesSearch updateFilters={setFilters} />
        </div>
        <div>
          <Hidden xlDown>
            <div className={classes.row}>
              {isAuthenticated && (
                <Button color='primary' component={Link} to={URLS.ADMIN_ACTIVITIES} variant='text'>
                  Opprett aktivitet
                </Button>
              )}
            </div>
          </Hidden>
          <Collapse in={view === 'list'}>
            <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
              <MasonryGrid>
                {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen aktiviteter'} />}
                {activities.map((activity) => (
                  <ActivityCard activity={activity} gutterBottom key={activity.id} />
                ))}
              </MasonryGrid>
            </Pagination>
          </Collapse>
          <Collapse in={view === 'calendar'} mountOnEnter>
            <Calendar activities={activities} />
          </Collapse>
          <Collapse in={view === 'map'} mountOnEnter>
            <ActivitiesMap hookArgs={filters} useHook={useActivities} />
          </Collapse>
        </div>
      </div>
    </Navigation>
  );
};

export default Activities;

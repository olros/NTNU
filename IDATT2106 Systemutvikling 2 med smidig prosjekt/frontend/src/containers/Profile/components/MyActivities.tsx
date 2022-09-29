import { useState, useMemo } from 'react';
import { useMyParticipatingActivities, useMyLikedActivities } from 'hooks/Activities';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import { Button, ButtonGroup, Collapse, Hidden, TextField, MenuItem } from '@material-ui/core';

// Icons
import FutureIcon from '@material-ui/icons/FastForwardRounded';
import HistoryIcon from '@material-ui/icons/HistoryRounded';
import LikedIcon from '@material-ui/icons/FavoriteRounded';

// Project components
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import ActivityCard from 'components/layout/ActivityCard';
import Calendar from 'components/miscellaneous/Calendar';
import ActivitiesMap from 'components/miscellaneous/ActivitiesMap';

const useStyles = makeStyles((theme) => ({
  menu: {
    display: 'grid',
    gap: theme.spacing(1),
    gridTemplateColumns: '3fr 1fr',
    [theme.breakpoints.down('md')]: {
      gridTemplateColumns: '1fr',
      paddingBottom: theme.spacing(1),
    },
  },
  buttons: {
    marginBottom: theme.spacing(1),
  },
  button: {
    paddingLeft: theme.spacing(1),
    paddingRight: theme.spacing(1),
    height: 56,
  },
  list: {
    display: 'grid',
    gap: theme.spacing(1),
    gridTemplateColumns: '1fr 1fr',
    [theme.breakpoints.down('md')]: {
      gridTemplateColumns: '1fr',
    },
  },
}));

export type MyActivitiesProps = {
  userId?: string;
};

type View = 'list' | 'calendar' | 'map';
type Type = 'future' | 'past' | 'favourites';

const MyActivities = ({ userId }: MyActivitiesProps) => {
  const classes = useStyles();
  const [type, setType] = useState<Type>('future');
  const [view, setView] = useState<View>('list');
  const filters = useMemo(
    () =>
      type === 'favourites'
        ? {}
        : {
            [type === 'past' ? 'registrationStartDateBefore' : 'registrationStartDateAfter']: new Date().toISOString(),
          },
    [type],
  );
  const useHook = type === 'favourites' ? useMyLikedActivities : useMyParticipatingActivities;
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useHook(userId, filters);
  const activities = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !activities.length && !isFetching, [activities, isFetching]);

  return (
    <>
      <div className={classes.menu}>
        <ButtonGroup aria-label='Hvilke aktiviteter vil du se?' className={classes.buttons} fullWidth>
          <Button
            className={classes.button}
            endIcon={
              <Hidden mdDown>
                <FutureIcon />
              </Hidden>
            }
            onClick={() => setType('future')}
            variant={type === 'future' ? 'contained' : 'outlined'}>
            Kommende
          </Button>
          <Button
            className={classes.button}
            endIcon={
              <Hidden mdDown>
                <HistoryIcon />
              </Hidden>
            }
            onClick={() => setType('past')}
            variant={type === 'past' ? 'contained' : 'outlined'}>
            Tidligere
          </Button>
          <Button
            className={classes.button}
            endIcon={
              <Hidden mdDown>
                <LikedIcon />
              </Hidden>
            }
            onClick={() => setType('favourites')}
            variant={type === 'favourites' ? 'contained' : 'outlined'}>
            Favoritter
          </Button>
        </ButtonGroup>
        <TextField fullWidth label='Visning' onChange={(e) => setView(e.target.value as View)} select value={view}>
          <MenuItem value='list'>Liste</MenuItem>
          <MenuItem value='calendar'>Kalender</MenuItem>
          <MenuItem value='map'>Kart</MenuItem>
        </TextField>
      </div>
      <Collapse in={view === 'list'}>
        <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
          {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen aktiviteter'} />}
          <div className={classes.list}>
            {activities.map((activity) => (
              <ActivityCard activity={activity} fullHeight key={activity.id} />
            ))}
          </div>
        </Pagination>
      </Collapse>
      <Collapse in={view === 'calendar'} mountOnEnter>
        <Calendar activities={activities} />
      </Collapse>
      <Collapse in={view === 'map'} mountOnEnter>
        <ActivitiesMap hookArgs={filters} useHook={useHook} userId={userId} />
      </Collapse>
    </>
  );
};

export default MyActivities;

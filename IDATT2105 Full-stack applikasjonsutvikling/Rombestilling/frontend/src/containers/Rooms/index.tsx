import { useMemo, useState } from 'react';
import Helmet from 'react-helmet';
import { useSections } from 'hooks/Section';
import { useUser } from 'hooks/User';
import classnames from 'classnames';
import { startOfHour, addHours } from 'date-fns';
import { isUserAdmin } from 'utils';

// Material UI Components
import { makeStyles, Typography, SwipeableDrawer, Button } from '@material-ui/core';

// Project Components
import Container from 'components/layout/Container';
import ReserveForm from 'components/miscellaneous/ReserveForm';
import RoomFilterBox from 'containers/Rooms/components/RoomFilterBox';
import RoomListItem from 'containers/Rooms/components/RoomListItem';
import Pagination from 'components/layout/Pagination';
import CreateRoom from 'components/miscellaneous/CreateRoom';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  top: {
    gridTemplateColumns: '1fr auto',
  },
  reservationPaper: {
    maxWidth: theme.breakpoints.values.md,
    margin: 'auto',
    padding: theme.spacing(3, 2, 5),
    borderRadius: `${theme.shape.borderRadius}px ${theme.shape.borderRadius}px 0 0`,
    background: theme.palette.background.paper,
  },
}));

export type RoomFilters = {
  name?: string;
  from?: string;
  to?: string;
};

const defaultFilters: Required<RoomFilters> = {
  name: '',
  from: startOfHour(addHours(new Date(), 1)).toJSON(),
  to: startOfHour(addHours(new Date(), 2)).toJSON(),
};

const Rooms = () => {
  const classes = useStyles();
  const { data: user } = useUser();
  const [filters, setFilters] = useState<RoomFilters>(defaultFilters);
  const finalFilters = useMemo(
    () => (filters.from && filters.to ? { name: filters.name, interval: `${filters.from}&interval=${filters.to}` } : { name: filters.name }),
    [filters],
  );
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useSections(finalFilters);
  const results = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !results.length && !isFetching, [results, isFetching]);
  const [reservationOpen, setReservationOpen] = useState(false);
  const [selectedSectionId, setSelectedSectionId] = useState<string | null>(null);
  const startReservation = async (sectionId: string) => {
    setSelectedSectionId(sectionId);
    setReservationOpen(true);
  };
  const stopReservation = () => {
    setReservationOpen(false);
    setSelectedSectionId(null);
  };
  return (
    <Container>
      <Helmet>
        <title>Reserver - Rombestilling</title>
      </Helmet>
      <div className={classes.list}>
        <div className={classnames(classes.list, classes.top)}>
          <Typography variant='h1'>Reserver</Typography>
          {isUserAdmin(user) && <CreateRoom>Opprett rom</CreateRoom>}
        </div>
        <RoomFilterBox defaultFilters={defaultFilters} filters={filters} updateFilters={setFilters} />
        <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
          <div className={classes.list}>
            {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen ledige rom'} />}
            {results.map((room) => (
              <RoomListItem key={room.id} reserve={startReservation} room={room} showReserve={Boolean(filters.to && filters.from)} />
            ))}
          </div>
        </Pagination>
      </div>
      <SwipeableDrawer
        anchor='bottom'
        classes={{ paper: classes.reservationPaper }}
        disableSwipeToOpen
        onClose={stopReservation}
        onOpen={() => setReservationOpen(true)}
        open={reservationOpen}
        swipeAreaWidth={56}>
        <div className={classes.list}>
          {selectedSectionId && filters.from && filters.to && (
            <ReserveForm from={filters.from} onConfirm={stopReservation} sectionId={selectedSectionId} to={filters.to} />
          )}
          <Button onClick={stopReservation} variant='text'>
            Avbryt
          </Button>
        </div>
      </SwipeableDrawer>
    </Container>
  );
};

export default Rooms;

import { useMemo } from 'react';
import { UseInfiniteQueryResult } from 'react-query';
import { Reservation, PaginationResponse, RequestResponse } from 'types/Types';
import { useSectionReservations, useUserReservations, useGroupReservations } from 'hooks/Reservation';
import { parseISO, startOfDay } from 'date-fns';
import { formatDate } from 'utils';

// Material UI Components
import { makeStyles, List } from '@material-ui/core';

// Project Components
import Expand from 'components/layout/Expand';
import ReservationInfo from 'components/miscellaneous/ReservationInfo';
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  paper: {
    overflow: 'hidden',
  },
  wrapper: {
    alignItems: 'center',
  },
  listContent: {
    padding: theme.spacing(1),
  },
}));

export type UserReservationsProps = {
  userId?: string;
};

export const UserReservations = ({ userId }: UserReservationsProps) => {
  const filters = useMemo(() => ({ fromTimeAfter: startOfDay(new Date()).toJSON() }), []);
  const result = useUserReservations(userId, filters);
  return <Reservations result={result} />;
};

export type GroupReservationsProps = {
  groupId: string;
};

export const GroupReservations = ({ groupId }: GroupReservationsProps) => {
  const filters = useMemo(() => ({ fromTimeAfter: startOfDay(new Date()).toJSON() }), []);
  const result = useGroupReservations(groupId, filters);
  return <Reservations result={result} />;
};

export type SectionReservationsProps = {
  sectionId: string;
};

export const SectionReservations = ({ sectionId }: SectionReservationsProps) => {
  const filters = useMemo(() => ({ fromTimeAfter: startOfDay(new Date()).toJSON() }), []);
  const result = useSectionReservations(sectionId, filters);
  return <Reservations result={result} />;
};

export type ReservationsProps = {
  result: UseInfiniteQueryResult<PaginationResponse<Reservation>, RequestResponse>;
};

const Reservations = ({ result: { data, error, hasNextPage, fetchNextPage, isFetching } }: ReservationsProps) => {
  const classes = useStyles();
  const reservations = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !reservations.length && !isFetching, [reservations, isFetching]);

  const Reservation = ({ reservation }: { reservation: Reservation }) => (
    <Expand primary={`${formatDate(parseISO(reservation.fromTime))} - ${formatDate(parseISO(reservation.toTime))}`}>
      <div className={classes.listContent}>
        <ReservationInfo reservationId={reservation.id} sectionId={reservation.section.id} />
      </div>
    </Expand>
  );

  return (
    <div className={classes.grid}>
      <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
        <List className={classes.grid} disablePadding>
          {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen reservasjoner'} />}
          {reservations.map((reservation) => (
            <Reservation key={reservation.id} reservation={reservation} />
          ))}
        </List>
      </Pagination>
    </div>
  );
};

export default Reservations;

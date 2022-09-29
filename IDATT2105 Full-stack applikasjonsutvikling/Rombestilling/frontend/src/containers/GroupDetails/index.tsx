import { useEffect, useState, lazy, Suspense } from 'react';
import Helmet from 'react-helmet';
import { useParams } from 'react-router-dom';
import { useGroup } from 'hooks/Group';
import { useUser } from 'hooks/User';
import classnames from 'classnames';
import { isUserAdmin } from 'utils';

// Material UI Components
import { makeStyles, Typography, Collapse } from '@material-ui/core';

// Icons
import CalendarIcon from '@material-ui/icons/EventRounded';
import ListIcon from '@material-ui/icons/ViewStreamRounded';
import UsersIcon from '@material-ui/icons/PeopleOutlineRounded';

// Project Components
import Container from 'components/layout/Container';
import Tabs from 'components/layout/Tabs';
import { GroupReservations } from 'containers/RoomDetails/components/RoomReservations';
import GroupMemberships from 'containers/GroupDetails/components/GroupMemberships';
import EditGroup from 'components/miscellaneous/EditGroup';
const Http404 = lazy(() => import(/* webpackChunkName: "http404" */ 'containers/Http404'));
const GroupCalendar = lazy(() => import(/* webpackChunkName: "group_calendar" */ 'components/miscellaneous/calendar/GroupCalendar'));

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
    alignItems: 'self-start',
  },
  top: {
    gridTemplateColumns: '1fr auto',
  },
}));

const GroupDetails = () => {
  const classes = useStyles();
  const { data: user } = useUser();
  const { id } = useParams();
  const { data, isLoading, isError } = useGroup(id);
  const isAdmin = isUserAdmin(user);
  const reservationsTab = { value: 'reservations', label: 'Reservasjoner', icon: ListIcon };
  const calendarTab = { value: 'calendar', label: 'Kalender', icon: CalendarIcon };
  const membersTab = { value: 'members', label: 'Medlemmer', icon: UsersIcon };
  const tabs = [reservationsTab, calendarTab, membersTab];
  const [tab, setTab] = useState(reservationsTab.value);

  useEffect(() => {
    setTab(reservationsTab.value);
  }, [id]);

  if (isError) {
    return (
      <Suspense fallback={null}>
        <Http404 />
      </Suspense>
    );
  }
  return (
    <Container>
      <Helmet>
        <title>{`${data?.name || 'Laster gruppe...'} - Rombestilling`}</title>
      </Helmet>
      {data && !isLoading && (
        <div className={classes.grid}>
          <div className={classnames(classes.grid, classes.top)}>
            <Typography variant='h1'>{data.name}</Typography>
            {isAdmin && <EditGroup group={data}>Endre gruppe</EditGroup>}
          </div>
          <div className={classes.grid}>
            <Tabs selected={tab} setSelected={setTab} tabs={tabs} />
            <div>
              <Collapse in={tab === reservationsTab.value} mountOnEnter>
                <GroupReservations groupId={id} />
              </Collapse>
              <Collapse in={tab === calendarTab.value} mountOnEnter>
                <Suspense fallback={null}>
                  <GroupCalendar groupId={id} />
                </Suspense>
              </Collapse>
              <Collapse in={tab === membersTab.value} mountOnEnter>
                <GroupMemberships groupId={id} />
              </Collapse>
            </div>
          </div>
        </div>
      )}
    </Container>
  );
};

export default GroupDetails;

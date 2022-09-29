import { useEffect, useState, lazy, Suspense } from 'react';
import Helmet from 'react-helmet';
import { useParams, useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser, useMakeAdmin, useLogout } from 'hooks/User';
import { isUserAdmin, urlEncode } from 'utils';

// Material UI Components
import { makeStyles, Typography, Avatar, Collapse, ListItem, ListItemAvatar, ListItemText, Button } from '@material-ui/core';

// Icons
import EditIcon from '@material-ui/icons/EditRounded';
import PostsIcon from '@material-ui/icons/ViewAgendaRounded';
import ListIcon from '@material-ui/icons/ViewStreamRounded';

// Project Components
import Container from 'components/layout/Container';
import Paper from 'components/layout/Paper';
import VerifyDialog from 'components/layout/VerifyDialog';
import Tabs from 'components/layout/Tabs';
import EditProfile from 'containers/Profile/components/EditProfile';
import { UserReservations } from 'containers/RoomDetails/components/RoomReservations';
const Http404 = lazy(() => import(/* webpackChunkName: "http404" */ 'containers/Http404'));
const UserCalendar = lazy(() => import(/* webpackChunkName: "user_calendar" */ 'components/miscellaneous/calendar/UserCalendar'));

const useStyles = makeStyles((theme) => ({
  avatar: {
    height: 90,
    width: 90,
    marginRight: theme.spacing(2),
    fontSize: '3rem',
  },
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
    alignItems: 'self-start',
  },
}));

const Profile = () => {
  const classes = useStyles();
  const { userId }: { userId?: string } = useParams();
  const { data: signedInUser } = useUser();
  const { data: user, isLoading, isError } = useUser(userId);
  const showSnackbar = useSnackbar();
  const makeUserAdmin = useMakeAdmin();
  const logout = useLogout();
  const reservationsTab = { value: 'reservations', label: 'Reservasjoner', icon: ListIcon };
  const bookings = { value: 'bookings', label: 'Kalender', icon: PostsIcon };
  const editTab = { value: 'edit', label: 'Rediger profil', icon: EditIcon };
  const tabs = [reservationsTab, bookings, editTab];
  const [tab, setTab] = useState(reservationsTab.value);
  const navigate = useNavigate();

  useEffect(() => {
    if (user && signedInUser && user.id === signedInUser.id) {
      navigate(`${URLS.PROFILE}`, { replace: true });
    } else if (userId && user) {
      navigate(`${URLS.USERS}${user.id}/${urlEncode(`${user.firstName} ${user.surname}`)}/`, { replace: true });
    }
  }, [user, signedInUser, navigate]);

  useEffect(() => {
    if (userId && signedInUser && !isUserAdmin(signedInUser)) {
      navigate(URLS.LANDING, { replace: true });
    }
  }, [signedInUser, userId]);

  if (isError) {
    return (
      <Suspense fallback={null}>
        <Http404 />
      </Suspense>
    );
  }
  if (isLoading || !user) {
    return null;
  }

  const makeAdmin = async () =>
    makeUserAdmin.mutate(user.id, {
      onSuccess: () => {
        showSnackbar('Brukeren ble gjort til administrator', 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });

  return (
    <Container>
      <Helmet>
        <title>{`${user.firstName} ${user.surname} - Rombestilling`}</title>
      </Helmet>
      <div className={classes.grid}>
        <ListItem component='div'>
          <ListItemAvatar>
            <Avatar className={classes.avatar} src={user.image}>{`${user.firstName.substr(0, 1)}${user.surname.substr(0, 1)}`}</Avatar>
          </ListItemAvatar>
          <ListItemText primary={<Typography variant='h1'>{`${user.firstName} ${user.surname}`}</Typography>} secondary={user.email} />
        </ListItem>
        {userId && !isUserAdmin(user) && (
          <VerifyDialog
            contentText='Denne brukeren vil få administrator-tilgang til hele systemet. Det innebærer å kunne se og redigere alle bruker, reservasjoner, rom og underseksjoner.'
            onConfirm={makeAdmin}
            titleText='Gjør til administrator'>
            Gjør til administrator
          </VerifyDialog>
        )}
        {!userId && (
          <Button color='secondary' fullWidth onClick={logout} variant='outlined'>
            Logg ut
          </Button>
        )}
        <div className={classes.grid}>
          <Tabs selected={tab} setSelected={setTab} tabs={tabs} />
          <div>
            <Collapse in={tab === reservationsTab.value} mountOnEnter>
              <UserReservations userId={userId} />
            </Collapse>
            <Collapse in={tab === bookings.value} mountOnEnter>
              <Suspense fallback={null}>
                <UserCalendar userId={userId} />
              </Suspense>
            </Collapse>
            <Collapse in={tab === editTab.value} mountOnEnter>
              <Paper>
                <EditProfile user={user} />
              </Paper>
            </Collapse>
          </div>
        </div>
      </div>
    </Container>
  );
};

export default Profile;

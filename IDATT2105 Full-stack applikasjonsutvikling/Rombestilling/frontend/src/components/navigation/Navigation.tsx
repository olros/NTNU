import { ComponentType, useMemo, ReactNode } from 'react';
import Helmet from 'react-helmet';
import { useIsAuthenticated, useUser } from 'hooks/User';
import { isUserAdmin } from 'utils';
import URLS from 'URLS';

// Material UI Components
import { makeStyles, LinearProgress, Hidden } from '@material-ui/core';

// Icons
import SearchIcon from '@material-ui/icons/SearchRounded';
import UsersIcon from '@material-ui/icons/PeopleOutlineRounded';
import GroupsIcon from '@material-ui/icons/GroupsRounded';
import ProfileIcon from '@material-ui/icons/AccountCircleRounded';
import LoginIcon from '@material-ui/icons/LoginRounded';

// Project Components
import BottomBar from 'components/navigation/BottomBar';
import SideMenu from 'components/navigation/SideMenu';

const useStyles = makeStyles((theme) => ({
  main: {
    minHeight: '100vh',
    display: 'grid',
    [theme.breakpoints.down('lg')]: {
      paddingBottom: 80,
    },
  },
  content: {
    [theme.breakpoints.up('lg')]: {
      marginLeft: 300,
    },
  },
}));

export type NavigationProps = {
  children?: ReactNode;
  isLoading?: boolean;
};

export type NavigationItem = {
  icon: ComponentType<{ className?: string }>;
  text: string;
  to: string;
};

const Navigation = ({ isLoading = false, children }: NavigationProps) => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();
  const { data: user } = useUser();

  const items = useMemo<Array<NavigationItem>>(
    () =>
      isAuthenticated
        ? [
            { icon: SearchIcon, text: 'Reserver', to: URLS.ROOMS },
            { icon: GroupsIcon, text: 'Grupper', to: URLS.GROUPS },
            ...(isUserAdmin(user) ? [{ icon: UsersIcon, text: 'Brukere', to: URLS.USERS }] : []),
            { icon: ProfileIcon, text: 'Profil', to: URLS.PROFILE },
          ]
        : [{ icon: LoginIcon, text: 'Logg inn', to: URLS.LOGIN }],
    [isAuthenticated, user],
  );

  return (
    <>
      <Helmet>
        <title>Rombestilling - Reserver et rom nå!</title>
      </Helmet>
      <main className={classes.main}>
        <Hidden lgDown>
          <SideMenu items={items} />
        </Hidden>
        <div className={classes.content}>{isLoading ? <LinearProgress /> : children}</div>
        <Hidden lgUp>
          <BottomBar items={items} />
        </Hidden>
      </main>
    </>
  );
};

export default Navigation;

import { useMemo, useState, useEffect } from 'react';
import classnames from 'classnames';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { useIsAuthenticated, useLogout } from 'hooks/User';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Hidden from '@material-ui/core/Hidden';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';

// Assets/Icons
import MenuIcon from '@material-ui/icons/MenuRounded';
import CloseIcon from '@material-ui/icons/CloseRounded';

// Project Components
import Sidebar from 'components/navigation/Sidebar';
import Logo from 'components/miscellaneous/Logo';
import ThemeSettings from 'components/miscellaneous/ThemeSettings';

const useStyles = makeStyles((theme) => ({
  appBar: {
    boxSizing: 'border-box',
    backgroundColor: theme.palette.colors.topbar,
    color: theme.palette.text.primary,
    flexGrow: 1,
    zIndex: theme.zIndex.drawer + 1,
  },
  transparentAppBar: {
    backgroundColor: 'transparent',
  },
  backdrop: {
    ...theme.palette.blurred,
    ...theme.palette.transparent,
    backgroundColor: `${theme.palette.colors.topbar}bf`,
    borderTop: 'none',
    borderRight: 'none',
    borderLeft: 'none',
  },
  toolbar: {
    width: '100%',
    margin: 'auto',
    padding: theme.spacing(0, 2),
    display: 'grid',
    gridTemplateColumns: '120px 1fr auto',
    [theme.breakpoints.down('md')]: {
      padding: theme.spacing(0, 1),
      gridTemplateColumns: '80px 1fr',
    },
  },

  items: {
    display: 'grid',
    gap: theme.spacing(1),
    alignItems: 'self-start',
    gridAutoFlow: 'column',
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
    width: 'fit-content',
  },
  right: {
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
    display: 'grid',
    gap: theme.spacing(1),
    gridTemplateColumns: '35px auto',
    [theme.breakpoints.down('md')]: {
      display: 'grid',
      justifyContent: 'flex-end',
    },
  },
  profileName: {
    margin: `auto ${theme.spacing(1)}px`,
    color: theme.palette.common.white,
    textAlign: 'right',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  topbarItem: {
    height: 35,
    margin: 'auto 0',
    color: 'inherit',
  },
  reverseColor: {
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
  },
}));

export type TopBarItemProps = {
  text: string;
  to: string;
};

const TopBarItem = ({ text, to }: TopBarItemProps) => {
  const classes = useStyles({});
  const partial = useMemo(() => location.pathname.substr(0, to.length) === to, [location.pathname, to]);
  const equal = useMemo(() => location.pathname === to, [location.pathname, to]);
  return (
    <Button
      className={classes.topbarItem}
      color='inherit'
      component={Link}
      onClick={equal ? () => window.location.reload() : undefined}
      to={to}
      variant={partial ? 'outlined' : 'text'}>
      {text}
    </Button>
  );
};

export type TopbarProps = {
  variant: 'transparent' | 'dynamic' | 'filled';
};

const Topbar = ({ variant }: TopbarProps) => {
  const isAuthenticated = useIsAuthenticated();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const classes = useStyles();
  const logout = useLogout();
  const [scrollLength, setScrollLength] = useState(0);

  const handleScroll = () => setScrollLength(window.pageYOffset);

  useEffect(() => {
    window.scrollTo(0, 0);
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollAtTop = useMemo(() => scrollLength < 20, [scrollLength]);

  const items = useMemo(
    () =>
      [
        { text: 'Aktiviteter', to: URLS.ACTIVITIES },
        { text: 'Brukere', to: URLS.USERS },
        { text: 'Om GIDD', to: URLS.ABOUT },
        ...(isAuthenticated ? [{ text: 'Min profil', to: URLS.PROFILE }] : []),
      ] as Array<TopBarItemProps>,
    [isAuthenticated],
  );

  return (
    <AppBar
      className={classnames(
        classes.appBar,
        variant !== 'filled' && scrollAtTop && !sidebarOpen && classes.transparentAppBar,
        (variant === 'filled' || !scrollAtTop) && !sidebarOpen && classes.backdrop,
      )}
      color='primary'
      elevation={0}
      position='fixed'>
      <Toolbar disableGutters>
        <div className={classes.toolbar}>
          <Link to={URLS.LANDING}>
            <Logo darkColor={'white'} lightColor={'black'} />
          </Link>
          <Hidden mdDown>
            <div className={classnames(classes.items, variant === 'dynamic' && scrollAtTop && classes.reverseColor)}>
              {items.map((item, i) => (
                <TopBarItem key={i} {...item} />
              ))}
            </div>
          </Hidden>
          <div className={classnames(classes.right, variant === 'dynamic' && scrollAtTop && classes.reverseColor)}>
            <Hidden mdDown>
              <ThemeSettings className={classes.topbarItem} />
              {isAuthenticated ? (
                <Button className={classes.topbarItem} color='inherit' onClick={logout} variant='outlined'>
                  Logg ut
                </Button>
              ) : (
                <Button className={classes.topbarItem} color='inherit' component={Link} to={URLS.LOGIN} variant='outlined'>
                  Logg inn
                </Button>
              )}
            </Hidden>
            <Hidden mdUp>
              <IconButton className={classes.topbarItem} onClick={() => setSidebarOpen((prev) => !prev)}>
                {sidebarOpen ? <CloseIcon aria-label='Lukk meny' /> : <MenuIcon aria-label='Åpne meny' />}
              </IconButton>
              <Sidebar items={items} onClose={() => setSidebarOpen(false)} open={sidebarOpen} />
            </Hidden>
          </div>
        </div>
      </Toolbar>
    </AppBar>
  );
};

export default Topbar;

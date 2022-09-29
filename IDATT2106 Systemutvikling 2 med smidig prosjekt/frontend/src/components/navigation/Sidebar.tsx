import URLS from 'URLS';
import { Link } from 'react-router-dom';
import { useIsAuthenticated, useLogout } from 'hooks/User';

// Material UI Components
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import Button from '@material-ui/core/Button';

// Project components
import ThemeSettings from 'components/miscellaneous/ThemeSettings';

const useStyles = makeStyles((theme) => ({
  sidebar: {
    background: theme.palette.colors.topbar,
    width: '100vw',
    overflow: 'auto',
    display: 'grid',
    gridTemplateRows: '1fr 70px',
    height: 'calc(100% - 64px)',
    marginTop: 64,
    [theme.breakpoints.down('xs')]: {
      height: 'calc(100% - 56px)',
      marginTop: 56,
    },
  },
  root: {
    padding: theme.spacing(5, 3),
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-evenly',
  },
  text: {
    color: theme.palette.getContrastText(theme.palette.colors.topbar),
    fontSize: '2rem',
    textTransform: 'none',
  },
  bottomButton: {
    height: 70,
    borderRadius: 0,
  },
  icon: {
    display: 'flex',
    justifyContent: 'center',
  },
  classNameIcon: {
    fontSize: '3rem',
  },
}));

type SidebarItemProps = {
  text: string;
  to: string;
};

const SidebarItem = ({ text, to }: SidebarItemProps) => {
  const classes = useStyles();
  return (
    <Button
      className={classes.text}
      component={Link}
      fullWidth
      onClick={to === window.location.pathname ? () => window.location.reload() : undefined}
      to={to}
      variant='text'>
      {text}
    </Button>
  );
};

export type IProps = {
  items: Array<SidebarItemProps>;
  onClose: () => void;
  open: boolean;
};

const Sidebar = ({ items, onClose, open }: IProps) => {
  const classes = useStyles();
  const isAuthenticated = useIsAuthenticated();
  const theme = useTheme();
  const logout = useLogout();
  return (
    <Drawer anchor='top' classes={{ paper: classes.sidebar }} onClose={onClose} open={open} style={{ zIndex: theme.zIndex.drawer - 1 }}>
      <div className={classes.root}>
        {items.map((item, i) => (
          <SidebarItem key={i} {...item} />
        ))}
        <div className={classes.icon}>
          <ThemeSettings className={classes.text} classNameIcon={classes.classNameIcon} />
        </div>
      </div>
      {isAuthenticated ? (
        <Button className={classes.bottomButton} color='secondary' fullWidth onClick={logout}>
          Logg ut
        </Button>
      ) : (
        <Button className={classes.bottomButton} color='secondary' component={Link} fullWidth to={URLS.LOGIN}>
          Logg inn
        </Button>
      )}
    </Drawer>
  );
};

export default Sidebar;

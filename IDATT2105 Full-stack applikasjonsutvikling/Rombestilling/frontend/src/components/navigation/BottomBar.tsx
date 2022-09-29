import { useState } from 'react';
import URLS from 'URLS';
import { Link, useLocation } from 'react-router-dom';

// Material UI Components
import { makeStyles, BottomNavigation, BottomNavigationAction } from '@material-ui/core';
import ThemeIcon from '@material-ui/icons/LightModeRounded';

// Project components
import Paper from 'components/layout/Paper';
import ThemeSettings from 'components/miscellaneous/ThemeSettings';
import { NavigationItem } from 'components/navigation/Navigation';

const useStyles = makeStyles((theme) => ({
  root: {
    position: 'fixed',
    bottom: 0,
    left: 0,
    right: 0,
    zIndex: 1000,
    borderBottomRightRadius: 0,
    borderBottomLeftRadius: 0,
    borderTopLeftRadius: 2 * Number(theme.shape.borderRadius),
    borderTopRightRadius: 2 * Number(theme.shape.borderRadius),
  },
  navbar: {
    height: 80,
    background: 'transparent',
    padding: theme.spacing(1, 0, 3),
  },
  action: {
    color: theme.palette.text.secondary,
    padding: 12,
    '&$selected': {
      color: theme.palette.text.primary,
    },
  },
  selected: {
    // This must be empty to override the selected style
  },
}));

export type BottomBarProps = {
  items: Array<NavigationItem>;
};

const THEME_TAB_KEY = 'theme';

const BottomBar = ({ items }: BottomBarProps) => {
  const classes = useStyles();
  const [themeOpen, setThemeOpen] = useState(false);
  const location = useLocation();
  const routeVal = (path: string) => {
    if (path.substring(0, URLS.ROOMS.length) === URLS.ROOMS) {
      return URLS.ROOMS;
    } else if (path.substring(0, URLS.GROUPS.length) === URLS.GROUPS) {
      return URLS.GROUPS;
    } else if (path.substring(0, URLS.USERS.length) === URLS.USERS) {
      return URLS.USERS;
    } else {
      return path;
    }
  };
  const [tab, setTab] = useState(routeVal(location.pathname));
  return (
    <Paper blurred className={classes.root} noPadding>
      <BottomNavigation
        className={classes.navbar}
        onChange={(event, newValue) => (items.some((item) => item.to === newValue) ? setTab(newValue) : null)}
        showLabels
        value={tab}>
        {items.map(({ text, to, icon: Icon }, i) => (
          <BottomNavigationAction
            classes={{ root: classes.action, selected: classes.selected }}
            component={Link}
            icon={<Icon />}
            key={i}
            label={text}
            to={to}
            value={to}
          />
        ))}
        <BottomNavigationAction
          classes={{ root: classes.action, selected: classes.selected }}
          icon={<ThemeIcon />}
          label='Tema'
          onClick={() => setThemeOpen(true)}
          value={THEME_TAB_KEY}
        />
      </BottomNavigation>
      <ThemeSettings onClose={() => setThemeOpen(false)} open={themeOpen} />
    </Paper>
  );
};

export default BottomBar;

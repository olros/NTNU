import { useMemo, useState } from 'react';
import URLS from 'URLS';
import classnames from 'classnames';
import { Link } from 'react-router-dom';

// Material UI Components
import { makeStyles, ListItem, ListItemText, ListItemIcon } from '@material-ui/core';
import ThemeIcon from '@material-ui/icons/LightModeRounded';

// Project components
import Paper from 'components/layout/Paper';
import ThemeSettings from 'components/miscellaneous/ThemeSettings';
import { NavigationItem } from 'components/navigation/Navigation';
import Logo from 'components/miscellaneous/Logo';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(5, 3),
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-start',
    borderTopLeftRadius: 0,
    borderBottomLeftRadius: 0,
    borderTopRightRadius: 2 * Number(theme.shape.borderRadius),
    borderBottomRightRadius: 2 * Number(theme.shape.borderRadius),
    position: 'fixed',
    top: 0,
    bottom: 0,
    left: 0,
    width: 300,
  },
  logo: {
    height: 50,
    width: 'auto',
    padding: theme.spacing(0.5, 1),
    marginBottom: theme.spacing(1),
  },
  itemPaper: {
    marginBottom: theme.spacing(3),
  },
  item: {
    padding: theme.spacing(2),
  },
  selectedBackground: {
    background: theme.palette.primary.light,
    color: theme.palette.getContrastText(theme.palette.primary.light),
  },
  selectedColor: {
    color: theme.palette.getContrastText(theme.palette.primary.light),
  },
}));

const SidebarItem = ({ icon: Icon, text, to }: NavigationItem) => {
  const classes = useStyles();
  const equal = useMemo(() => location.pathname === to, [location.pathname, to]);
  return (
    <Paper className={classnames(classes.itemPaper, equal && classes.selectedBackground, equal && classes.selectedBackground)} noPadding>
      <ListItem button className={classes.item} component={Link} to={to}>
        <ListItemIcon className={classnames(equal && classes.selectedColor)}>
          <Icon />
        </ListItemIcon>
        <ListItemText primary={text} />
      </ListItem>
    </Paper>
  );
};

export type SideMenuProps = {
  items: Array<NavigationItem>;
};

const SideMenu = ({ items }: SideMenuProps) => {
  const classes = useStyles();
  const [themeOpen, setThemeOpen] = useState(false);
  return (
    <Paper className={classes.root}>
      <Link to={URLS.LANDING}>
        <Logo className={classes.logo} />
      </Link>
      {items.map((item, i) => (
        <SidebarItem key={i} {...item} />
      ))}
      <Paper className={classes.itemPaper} noPadding>
        <ListItem button className={classes.item} onClick={() => setThemeOpen(true)}>
          <ListItemIcon>
            <ThemeIcon />
          </ListItemIcon>
          <ListItemText primary='Tema' />
        </ListItem>
      </Paper>
      <ThemeSettings onClose={() => setThemeOpen(false)} open={themeOpen} />
    </Paper>
  );
};

export default SideMenu;

import { ComponentType } from 'react';

// Material UI Components
import { makeStyles, Tabs as MaterialTabs, Tab } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  tabsFlexContainer: {
    position: 'relative',
    zIndex: 1,
  },
  tabsIndicator: {
    top: 0,
    bottom: 0,
    height: 'auto',
    background: 'none',
    '&:after': {
      content: '""',
      display: 'block',
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      borderRadius: theme.shape.borderRadius,
      backgroundColor: `${theme.palette.primary.light}80`,
    },
  },
  selected: {
    color: `${theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white })} !important`,
  },
  tabRoot: {
    color: theme.palette.text.primary,
    borderRadius: theme.shape.borderRadius,
    '&:hover': {
      opacity: 1,
    },
    padding: theme.spacing(2),
    minHeight: 44,
    minWidth: 96,
    [theme.breakpoints.up('md')]: {
      minWidth: 120,
    },
  },
  tabWrapper: {
    color: 'inherit',
    textTransform: 'initial',
    whiteSpace: 'nowrap',
  },
  marginBottom: {
    marginBottom: theme.spacing(2),
  },
  icon: {
    verticalAlign: 'middle',
    marginRight: 7,
    marginBottom: 3,
  },
}));

const a11yProps = (value: string | number) => {
  return {
    id: `simple-tab-${value}`,
    'aria-controls': `tabpanel-${value}`,
  };
};

export type IProps = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  selected: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  setSelected: (newSelected: any) => void;
  tabs: Array<{
    icon?: ComponentType<{ className: string }>;
    label: string;
    value: number | string;
  }>;
  marginBottom?: boolean;
};

const Tabs = ({ tabs, selected, setSelected }: IProps) => {
  const classes = useStyles();

  return (
    <MaterialTabs
      aria-label='Tabs'
      classes={{ flexContainer: classes.tabsFlexContainer, indicator: classes.tabsIndicator }}
      onChange={(e, newTab) => setSelected(newTab)}
      value={selected}
      variant='scrollable'>
      {tabs.map((tab, index) => (
        <Tab
          classes={{ root: classes.tabRoot, wrapper: classes.tabWrapper, selected: classes.selected }}
          key={index}
          label={
            <div>
              {tab.icon && <tab.icon className={classes.icon} />}
              {tab.label}
            </div>
          }
          value={tab.value}
          {...a11yProps(tab.value)}
        />
      ))}
    </MaterialTabs>
  );
};

export default Tabs;

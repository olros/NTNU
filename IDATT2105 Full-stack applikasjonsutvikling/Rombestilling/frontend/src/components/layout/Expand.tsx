import { useState, ReactNode } from 'react';

// Material UI Components
import { makeStyles, Collapse, Divider, ListItem, ListItemText, ListItemTextProps } from '@material-ui/core';

// Icons
import ExpandMoreIcon from '@material-ui/icons/ExpandMoreRounded';
import ExpandLessIcon from '@material-ui/icons/ExpandLessRounded';

// Project Components
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  paper: {
    overflow: 'hidden',
  },
  wrapper: {
    alignItems: 'center',
  },
  content: {
    display: 'grid',
    gap: theme.spacing(1),
    padding: theme.spacing(1),
  },
}));

export type ExpandProps = ListItemTextProps & {
  open?: boolean;
  toggleOpen?: () => void;
  children?: ReactNode;
};

const Expand = ({ children, open, toggleOpen, ...props }: ExpandProps) => {
  const classes = useStyles();
  const [isOpen, setIsOpen] = useState(false);
  return (
    <Paper className={classes.paper} noPadding>
      <ListItem button className={classes.wrapper} onClick={() => (toggleOpen ? toggleOpen() : setIsOpen((prev) => !prev))}>
        <ListItemText {...props} />
        {open || isOpen ? <ExpandLessIcon /> : <ExpandMoreIcon />}
      </ListItem>
      <Collapse in={open || isOpen} mountOnEnter unmountOnExit>
        <Divider />
        <div className={classes.content}>{children}</div>
      </Collapse>
    </Paper>
  );
};

export default Expand;

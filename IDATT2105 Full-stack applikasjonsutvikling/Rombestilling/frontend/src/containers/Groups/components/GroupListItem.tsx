import classnames from 'classnames';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { Group } from 'types/Types';

// Material UI Components
import { makeStyles, Button, Typography } from '@material-ui/core';

// Icons
import ArrowIcon from '@material-ui/icons/ArrowForwardRounded';

// Project Components
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  paper: {
    padding: theme.spacing(2, 3),
  },
  top: {
    gridTemplateColumns: '1fr auto',
    alignItems: 'center',
  },
}));

export type GroupListItemProps = {
  group: Group;
};

const GroupListItem = ({ group }: GroupListItemProps) => {
  const classes = useStyles();
  return (
    <Paper className={classnames(classes.paper, classes.grid)}>
      <div className={classnames(classes.top, classes.grid)}>
        <Typography variant='h3'>{`${group.name}`}</Typography>
        <Button component={Link} endIcon={<ArrowIcon />} to={`${URLS.GROUPS}${group.id}/`} variant='outlined'>
          Åpne
        </Button>
      </div>
    </Paper>
  );
};

export default GroupListItem;

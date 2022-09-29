import classnames from 'classnames';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { SectionChild } from 'types/Types';

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
  },
  topText: {
    gap: 0,
  },
}));

export type RoomSectionProps = {
  section: SectionChild;
};

const RoomSection = ({ section }: RoomSectionProps) => {
  const classes = useStyles();
  const isRoom = section.type === 'room';
  return (
    <Paper className={classnames(classes.paper, classes.grid)}>
      <div className={classnames(classes.top, classes.grid)}>
        <div className={classnames(classes.topText, classes.grid)}>
          <Typography variant='h3'>{section.name}</Typography>
          <Typography variant='caption'>{`${isRoom ? 'Rom' : 'Del av rom'} | Kapasitet: ${section.capacity}`}</Typography>
        </div>
        <Button component={Link} endIcon={<ArrowIcon />} to={`${URLS.ROOMS}${section.id}/`} variant='outlined'>
          {isRoom ? 'Åpne rom' : 'Åpne del'}
        </Button>
      </div>
    </Paper>
  );
};

export default RoomSection;

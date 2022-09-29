import { useState } from 'react';
import classnames from 'classnames';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { SectionList } from 'types/Types';

// Material UI Components
import { makeStyles, Button, Typography, Collapse, List, ListItem, ListItemText, ListItemSecondaryAction } from '@material-ui/core';

// Icons
import OpenIcon from '@material-ui/icons/OpenInNewRounded';
import ArrowIcon from '@material-ui/icons/ArrowForwardRounded';
import ExpandMoreIcon from '@material-ui/icons/ExpandMoreRounded';
import ExpandLessIcon from '@material-ui/icons/ExpandLessRounded';

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
  actions: {
    gridAutoFlow: 'column',
  },
  button: {
    height: 40,
  },
}));

export type RoomListItemProps = {
  room: SectionList;
  reserve: (sectionId: string) => void;
  showReserve?: boolean;
};

const RoomListItem = ({ room, reserve, showReserve }: RoomListItemProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const isRoom = room.type === 'room';
  return (
    <Paper className={classnames(classes.paper, classes.grid)}>
      <div className={classnames(classes.top, classes.grid)}>
        <div className={classnames(classes.topText, classes.grid)}>
          <Typography variant='h3'>{room.name}</Typography>
          <Typography variant='caption'>{`${isRoom ? 'Rom' : 'Del av rom'} | Kapasitet: ${room.capacity}`}</Typography>
        </div>
        {showReserve && (
          <Button endIcon={<ArrowIcon />} onClick={() => reserve(room.id)} variant='outlined'>
            Reserver
          </Button>
        )}
      </div>
      <div className={classnames(classes.actions, classes.grid)}>
        {((room.type === 'room' && Boolean(room.children.length)) || room.type === 'section') && (
          <Button
            className={classes.button}
            color='secondary'
            endIcon={open ? <ExpandLessIcon /> : <ExpandMoreIcon />}
            onClick={() => setOpen((prev) => !prev)}
            variant='text'>
            {isRoom ? 'Seksjoner' : 'Rom'}
          </Button>
        )}
        <Button
          className={classes.button}
          color='secondary'
          component={Link}
          endIcon={<OpenIcon />}
          target='_blank'
          to={`${URLS.ROOMS}${room.id}/`}
          variant='text'>
          Åpne rom
        </Button>
      </div>
      <Collapse in={open}>
        <List className={classes.grid} disablePadding>
          {room.type === 'room' ? (
            room.children.map((section) => (
              <ListItem component={Paper} key={section.id} noPadding>
                <ListItemText primary={section.name} secondary={`Kapasitet: ${section.capacity}`} />
                {showReserve && (
                  <ListItemSecondaryAction>
                    <Button className={classes.button} endIcon={<ArrowIcon />} onClick={() => reserve(section.id)} variant='outlined'>
                      Reserver
                    </Button>
                  </ListItemSecondaryAction>
                )}
              </ListItem>
            ))
          ) : (
            <ListItem component={Paper} noPadding>
              <ListItemText primary={room.parent.name} secondary={`Kapasitet: ${room.parent.capacity}`} />
              <ListItemSecondaryAction className={classnames(classes.actions, classes.grid)}>
                <Button className={classes.button} component={Link} endIcon={<OpenIcon />} target='_blank' to={`${URLS.ROOMS}${room.id}/`} variant='outlined'>
                  Åpne
                </Button>
              </ListItemSecondaryAction>
            </ListItem>
          )}
        </List>
      </Collapse>
    </Paper>
  );
};

export default RoomListItem;

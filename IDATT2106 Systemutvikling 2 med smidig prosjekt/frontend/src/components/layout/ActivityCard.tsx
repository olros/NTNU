import classnames from 'classnames';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Card, CardActionArea, CardContent, CardMedia, Typography } from '@material-ui/core/';

// Project Components
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import { ActivityList } from 'types/Types';

// Images
import LOGO from 'assets/img/DefaultBackground.jpg';

const useStyles = makeStyles((theme) => ({
  root: {
    ...theme.palette.transparent,
  },
  fullHeight: {
    height: '100%',
  },
  gutterBottom: {
    marginBottom: theme.spacing(1),
  },
  media: {
    height: 140,
  },
  link: {
    textDecoration: 'none',
  },
  title: {
    fontSize: theme.typography.h3.fontSize,
  },
  description: {
    overflow: 'hidden',
    '-webkit-line-clamp': 4,
    display: '-webkit-box',
    '-webkit-box-orient': 'vertical',
    whiteSpace: 'break-spaces',
  },
}));

export type ActivityCardProps = {
  activity: ActivityList;
  fullHeight?: boolean;
  gutterBottom?: boolean;
};

const ActivityCard = ({ activity, fullHeight, gutterBottom }: ActivityCardProps) => {
  const classes = useStyles();

  return (
    <Card className={classnames(classes.root, fullHeight && classes.fullHeight, gutterBottom && classes.gutterBottom)}>
      <CardActionArea component={Link} to={`${URLS.ACTIVITIES}${activity.id}/`}>
        <CardMedia className={classes.media} image={activity.images[0]?.url || LOGO} />
        <CardContent>
          <Typography className={classes.title} gutterBottom variant='h2'>
            {activity.title}
          </Typography>
          <Typography className={classes.description} variant='body2'>
            {activity.description}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default ActivityCard;

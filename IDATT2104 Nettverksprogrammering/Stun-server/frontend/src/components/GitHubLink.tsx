// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Images
import GITHUB from 'assets/img/GitHubWhite.png';

const useStyles = makeStyles(() => ({
  github: {
    display: 'flex',
    flexDirection: 'column',
    margin: 'auto',
    paddingTop: 20,
  },
  img: {
    width: 40,
    height: 40,
    margin: 'auto',
  },
}));

const GitHubLink = () => {
  const classes = useStyles();

  return (
    <a className={classes.github} href='https://github.com/olros/IDATT2104-Project' target='_noopener'>
      <img alt='Github' className={classes.img} src={GITHUB} />
      <Typography variant='subtitle1'>@IDATT2104-Project</Typography>
    </a>
  );
};

export default GitHubLink;

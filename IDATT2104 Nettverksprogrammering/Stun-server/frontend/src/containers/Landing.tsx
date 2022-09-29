import { useState, useRef, useEffect } from 'react';
import URLS from 'URLS';
import { useStore } from 'store';
import { Link, useNavigate } from 'react-router-dom';
import { openUserMedia, roomExists, createRoom } from 'api/FirebaseService';
import { useSnackbar } from 'Snackbar';

// Components
import GitHubLink from 'components/GitHubLink';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';

// Icons
import Add from '@material-ui/icons/Add';
import Group from '@material-ui/icons/Group';
import PermCameraMic from '@material-ui/icons/PermCameraMic';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(2),
    width: '100%',
    display: 'flex',
    flexDirection: 'column',
  },
  paper: {
    maxWidth: 700,
    margin: 'auto',
    width: '100%',
    padding: theme.spacing(3),
    marginBottom: theme.spacing(3),
  },
  flex: {
    display: 'flex',
    flexDirection: 'column',
    width: '100%',
  },
  header: {
    paddingTop: theme.spacing(4),
    paddingBottom: theme.spacing(3),
    textAlign: 'center',
    fontWeight: 600,
  },
  subtitle: {
    textAlign: 'center',
    marginBottom: theme.spacing(1),
  },
  options: {
    display: 'flex',
    marginTop: theme.spacing(2),
    [theme.breakpoints.down('md')]: {
      flexDirection: 'column',
    },
  },
  option: {
    width: '100%',
    height: 60,
    margin: theme.spacing(0, 1),
    fontSize: 17,
    [theme.breakpoints.down('md')]: {
      margin: theme.spacing(1, 0),
    },
  },
  video: {
    width: '100%',
    marginBottom: theme.spacing(2),
    borderRadius: theme.shape.borderRadius,
    transform: 'rotateY(180deg)',
  },
  hide: {
    display: 'none',
  },
  button: {
    height: 60,
  },
}));

const Landing = () => {
  const classes = useStyles();
  const store = useStore();
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const localVideo = useRef<HTMLVideoElement>(null);
  const [join, setJoin] = useState(false);

  useEffect(() => {
    if (localVideo?.current) {
      localVideo.current.srcObject = store.localStream.get;
    }
  }, [store.localStream.get]);

  const joinRoom = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const roomId = store.roomId.get;
    if (join && roomId) {
      if (await roomExists(roomId)) {
        navigate(`${URLS.room}${roomId}/`);
      } else {
        showSnackbar('Dette rommet eksisterer ikke', 'warning');
      }
    } else if (store.localStream.get !== null) {
      const roomId = await createRoom(store);
      navigate(`${URLS.room}${roomId}/`);
    } else {
      showSnackbar('Du må gi tilgang til kamera og mikrofon', 'warning');
    }
  };

  return (
    <div className={classes.root}>
      <Paper className={classes.paper} elevation={3}>
        <Link to={URLS.landing}>
          <Typography className={classes.header} variant='h1'>
            Snakk
          </Typography>
        </Link>
        <Typography className={classes.subtitle} variant='subtitle1'>
          Velkommen til Snakk!
          <br />
          Her kan du enkelt lage en videosamtale med andre.
          <br />
          Bare lag et rom og del linken din.
        </Typography>
        <div className={classes.options}>
          <Button
            className={classes.option}
            color={!join ? 'primary' : 'secondary'}
            onClick={() => setJoin(false)}
            startIcon={<Add />}
            variant={!join ? 'contained' : 'outlined'}>
            Lag nytt rom
          </Button>
          <Button
            className={classes.option}
            color={join ? 'primary' : 'secondary'}
            onClick={() => setJoin(true)}
            startIcon={<Group />}
            variant={join ? 'contained' : 'outlined'}>
            Bli med i et rom
          </Button>
        </div>
      </Paper>
      <Paper className={classes.paper} elevation={3}>
        <Typography className={classes.subtitle} variant='h5'>
          {join ? 'Bli med i rom' : 'Lag nytt rom'}
        </Typography>
        <form autoComplete='off' className={classes.flex} onSubmit={joinRoom}>
          {join ? (
            <>
              <TextField
                label='Skriv inn rom-ID'
                margin='normal'
                onChange={(event) => store.roomId.set(event.target.value)}
                required
                value={store.roomId.get || ''}
                variant='outlined'
              />
              <Button className={classes.button} color='primary' startIcon={<Group />} type='submit' variant='contained'>
                Bli med
              </Button>
            </>
          ) : (
            <>
              <TextField
                label='Skriv inn ditt navn'
                margin='normal'
                onChange={(event) => store.name.set(event.target.value)}
                required
                value={store.name.get || ''}
                variant='outlined'
              />
              <Button
                className={store.localStream.get ? classes.hide : classes.button}
                color='primary'
                onClick={() => openUserMedia(localVideo, store)}
                startIcon={<PermCameraMic />}
                variant='contained'>
                Åpne kamera og mikrofon
              </Button>
              {!store.localStream.get && (
                <Typography variant='subtitle2'>
                  {`Du må gi oss tilgang til ditt kamera og mikrofon før du kan opprette et rom. Trykk på "Åpne kamera og mikrofon" og klikk på "Tillat"`}
                </Typography>
              )}
              <video autoPlay className={!store.localStream.get ? classes.hide : classes.video} muted playsInline ref={localVideo}></video>
              {store.localStream.get && (
                <Button className={classes.button} color='primary' startIcon={<Add />} type='submit' variant='contained'>
                  Lag nytt rom
                </Button>
              )}
            </>
          )}
        </form>
      </Paper>
      <GitHubLink />
    </div>
  );
};

export default Landing;

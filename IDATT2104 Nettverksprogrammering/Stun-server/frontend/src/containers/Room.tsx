import { useState, useRef, useEffect } from 'react';
import URLS from 'URLS';
import { useStore } from 'store';
import { Link, useParams } from 'react-router-dom';
import { openUserMedia, roomExists, joinRoomById, hangUp, setScreenUserMedia, setCameraUserMedia } from 'api/FirebaseService';
import classnames from 'classnames';
import { useSnackbar } from 'Snackbar';

// Components
import ShareDialog from 'components/ShareDialog';
import GitHubLink from 'components/GitHubLink';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Fab from '@material-ui/core/Fab';
import Tooltip from '@material-ui/core/Tooltip';

// Icons
import ShareRoundedIcon from '@material-ui/icons/ShareRounded';
import ScreenShareRoundedIcon from '@material-ui/icons/ScreenShareRounded';
import CameraAltRoundedIcon from '@material-ui/icons/CameraAltRounded';
import Group from '@material-ui/icons/Group';
import PermCameraMic from '@material-ui/icons/PermCameraMic';
import CallEndRoundedIcon from '@material-ui/icons/CallEndRounded';

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
  videoButton: {
    height: 40,
    marginBottom: theme.spacing(3),
  },
  video: {
    width: '100%',
    marginBottom: theme.spacing(3),
    borderRadius: theme.shape.borderRadius,
    transform: 'rotateY(180deg)',
  },
  remoteVideo: {
    width: '100%',
    height: `calc(100vh - ${theme.spacing(6)}px)`,
    objectFit: 'cover',
    margin: 'auto',
    borderRadius: theme.shape.borderRadius,
  },
  localVideo: {
    width: 300,
    maxWidth: '50%',
    position: 'fixed',
    right: 0,
    bottom: 0,
    transform: 'rotateY(180deg)',
    borderTopRightRadius: theme.shape.borderRadius,
    border: '1px solid #111',
  },
  remoteName: {
    top: theme.spacing(3),
    right: theme.spacing(3),
    position: 'fixed',
    textShadow: '0px 0px 10px #fff',
    color: '#000',
  },
  duration: {
    top: theme.spacing(3),
    left: theme.spacing(3),
    position: 'fixed',
    textShadow: '0px 0px 10px #fff',
    color: '#000',
  },
  hide: {
    display: 'none',
  },
  button: {
    height: 60,
  },
  fabContainer: {
    position: 'fixed',
    left: theme.spacing(3),
    bottom: theme.spacing(3),
    display: 'flex',
    flexDirection: 'row',
    [theme.breakpoints.down('md')]: {
      flexDirection: 'column-reverse',
    },
  },
  fab: {
    margin: theme.spacing(1),
  },
  redFab: {
    backgroundColor: '#a30000',
  },
}));

const Room = () => {
  const classes = useStyles();
  const store = useStore();
  const { id: roomId } = useParams();
  const showSnackbar = useSnackbar();
  const [shareDialogOpen, setShareDialogOpen] = useState(false);
  const [joined, setJoined] = useState(store.name.get !== null && store.localStream.get !== null);
  const [duration, setDuration] = useState<number | null>(null);
  const previewVideo = useRef<HTMLVideoElement>(null);
  const localVideo = useRef<HTMLVideoElement>(null);
  const remoteVideo = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    if (store.remoteName.get !== null) {
      setInterval(() => setDuration((prevDuration) => (prevDuration !== null ? prevDuration + 1 : 0)), 1000);
    }
  }, [store.remoteName.get]);

  useEffect(() => {
    if (joined) {
      if (remoteVideo.current) {
        remoteVideo.current.srcObject = store.remoteStream.get;
      }
      if (localVideo.current) {
        localVideo.current.srcObject = store.localStream.get;
      }
    }
  }, [joined, store.remoteStream.get, store.localStream.get]);
  const joinRoom = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (store.localStream.get) {
      if (await roomExists(roomId)) {
        joinRoomById(roomId, store);
        setJoined(true);
      } else {
        showSnackbar('Dette rommet eksisterer ikke', 'warning');
      }
    } else {
      showSnackbar('Du må gi tilgang til kamera og mikrofon', 'warning');
    }
  };

  const share = () => {
    if (navigator.share) {
      navigator.share({
        title: 'Snakk med meg',
        url: window.location.href,
      });
    } else {
      setShareDialogOpen(true);
    }
  };

  const formatTime = (time: number | null) => {
    if (time) {
      const hours = Math.floor(time / 60 / 60);
      const minutes = Math.floor(time / 60) - hours * 60;
      const seconds = time % 60;
      return hours.toString().padStart(2, '0') + ':' + minutes.toString().padStart(2, '0') + ':' + seconds.toString().padStart(2, '0');
    } else {
      return '00:00:00';
    }
  };

  return (
    <div className={classes.root}>
      {joined ? (
        <>
          <video autoPlay className={!store.remoteStream.get?.active ? classes.hide : classes.remoteVideo} playsInline ref={remoteVideo}></video>
          <video autoPlay className={!store.localStream.get?.active ? classes.hide : classes.localVideo} muted playsInline ref={localVideo}></video>
          <Typography className={classes.remoteName} variant='h6'>
            {store.remoteName.get}
          </Typography>
          <Typography className={classes.duration} variant='h6'>
            {formatTime(duration)}
          </Typography>
          <div className={classes.fabContainer}>
            <Tooltip aria-label='Legg på' title='Legg på'>
              <Fab className={classnames(classes.fab, classes.redFab)} color='inherit' onClick={() => hangUp(store)}>
                <CallEndRoundedIcon />
              </Fab>
            </Tooltip>
            {!store.remoteStream.get?.active && (
              <Tooltip aria-label='Del link' title='Del link'>
                <Fab className={classes.fab} color='primary' onClick={() => share()}>
                  <ShareRoundedIcon />
                </Fab>
              </Tooltip>
            )}
            {!store.sharingDisplay.get ? (
              <Tooltip aria-label='Del skjerm' title='Del skjerm'>
                <Fab className={classes.fab} color='primary' onClick={() => setScreenUserMedia(localVideo, store, showSnackbar)}>
                  <ScreenShareRoundedIcon />
                </Fab>
              </Tooltip>
            ) : (
              <Tooltip aria-label='Del kamera' title='Del kamera'>
                <Fab className={classes.fab} color='primary' onClick={() => setCameraUserMedia(localVideo, store)}>
                  <CameraAltRoundedIcon />
                </Fab>
              </Tooltip>
            )}
          </div>
        </>
      ) : (
        <>
          <Paper className={classes.paper} elevation={3}>
            <form autoComplete='off' className={classes.flex} onSubmit={joinRoom}>
              <Link to={URLS.landing}>
                <Typography className={classes.header} variant='h1'>
                  Snakk
                </Typography>
              </Link>
              <Typography className={classes.subtitle} variant='subtitle1'>
                Velkommen til Snakk!
                <br />
                Skriv inn ditt navn og del kamera for å bli med.
              </Typography>
              <TextField label='Skriv inn ditt navn' margin='normal' onChange={(event) => store.name.set(event.target.value)} required variant='outlined' />
              <Button
                className={store.localStream.get ? classes.hide : classes.button}
                color='primary'
                onClick={() => openUserMedia(previewVideo, store)}
                startIcon={<PermCameraMic />}
                variant='contained'>
                Åpne kamera og mikrofon
              </Button>
              {!store.localStream.get && (
                <Typography variant='subtitle2'>
                  {`Du må gi oss tilgang til ditt kamera og mikrofon før du kan opprette et rom. Trykk på "Åpne kamera og mikrofon" og klikk på "Tillat"`}
                </Typography>
              )}
              <video autoPlay className={!store.localStream.get ? classes.hide : classes.video} muted playsInline ref={previewVideo}></video>
              {store.localStream.get && (
                <Button className={classes.button} color='primary' startIcon={<Group />} type='submit' variant='contained'>
                  Bli med
                </Button>
              )}
            </form>
          </Paper>
          <GitHubLink />
        </>
      )}
      <ShareDialog onClose={() => setShareDialogOpen(false)} open={shareDialogOpen} url={window.location.href} />
    </div>
  );
};

export default Room;

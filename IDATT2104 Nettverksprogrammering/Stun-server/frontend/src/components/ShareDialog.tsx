import { useRef } from 'react';
import { useSnackbar } from 'Snackbar';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Avatar from '@material-ui/core/Avatar';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';

// Icons
import LinkRoundedIcon from '@material-ui/icons/LinkRounded';
import SendRoundedIcon from '@material-ui/icons/SendRounded';
import MailOutlineRoundedIcon from '@material-ui/icons/MailOutlineRounded';
import ChatRoundedIcon from '@material-ui/icons/ChatRounded';

const useStyles = makeStyles((theme) => ({
  primary: {
    color: theme.palette.primary.contrastText,
    backgroundColor: theme.palette.primary.main,
  },
  textarea: {
    position: 'fixed',
    top: '-99999px',
  },
}));

export type ShareDialogProps = {
  onClose: () => void;
  open: boolean;
  url: string;
};

const ShareDialog = ({ onClose, open, url }: ShareDialogProps) => {
  const classes = useStyles();
  const textarea = useRef<HTMLInputElement>(null);
  const showSnackbar = useSnackbar();

  const handleClose = () => {
    onClose();
  };

  const copy = () => {
    textarea.current?.select();
    document.execCommand('copy');
    showSnackbar('Linken er kopiert til utklippstavlen', 'info');
    onClose();
  };
  const shareSMS = () => {
    window.open('sms:?body=Hei!%0ASnakk%20med%20meg%20på:%20' + encodeURIComponent(url), '_blank');
    onClose();
  };
  const shareEmail = () => {
    window.open('mailto:?subject=Snakk%20med%20meg&body=Hei!%0ASnakk%20med%20meg%20på:%20' + encodeURIComponent(url), '_blank');
    onClose();
  };
  const shareFacebook = () => {
    window.open('https://www.facebook.com/sharer.php?u=' + encodeURIComponent(url), '_blank');
    onClose();
  };

  return (
    <Dialog aria-labelledby='Del' onClose={handleClose} open={open}>
      <DialogTitle id='simple-dialog-title'>Del link</DialogTitle>
      <List>
        <ListItem button onClick={() => copy()}>
          <input className={classes.textarea} readOnly ref={textarea} type='text' value={url} />
          <ListItemAvatar>
            <Avatar className={classes.primary}>
              <LinkRoundedIcon />
            </Avatar>
          </ListItemAvatar>
          <ListItemText primary='Kopier link' />
        </ListItem>
        <ListItem button onClick={() => shareSMS()}>
          <ListItemAvatar>
            <Avatar className={classes.primary}>
              <SendRoundedIcon />
            </Avatar>
          </ListItemAvatar>
          <ListItemText primary='SMS' />
        </ListItem>
        <ListItem button onClick={() => shareEmail()}>
          <ListItemAvatar>
            <Avatar className={classes.primary}>
              <MailOutlineRoundedIcon />
            </Avatar>
          </ListItemAvatar>
          <ListItemText primary='Epost' />
        </ListItem>
        <ListItem button onClick={() => shareFacebook()}>
          <ListItemAvatar>
            <Avatar className={classes.primary}>
              <ChatRoundedIcon />
            </Avatar>
          </ListItemAvatar>
          <ListItemText primary='Facebook' />
        </ListItem>
      </List>
    </Dialog>
  );
};

export default ShareDialog;

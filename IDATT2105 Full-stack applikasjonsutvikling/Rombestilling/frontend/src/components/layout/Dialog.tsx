import { ReactNode } from 'react';

// Material UI Components
import { makeStyles, Button, Dialog as MaterialDialog, DialogActions, DialogTitle, DialogContent, DialogContentText } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  contentText: {
    color: theme.palette.text.secondary,
    whiteSpace: 'break-spaces',
  },
  paper: {
    background: theme.palette.background.paper,
  },
}));

export type DialogProps = {
  open: boolean;
  onClose: () => void;
  onCancel?: () => void;
  onConfirm?: () => void;
  titleText?: string;
  children?: ReactNode;
  contentText?: string;
  closeText?: string;
  confirmText?: string;
  disabled?: boolean;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | false;
  fullWidth?: boolean;
};

const Dialog = ({
  maxWidth = 'md',
  fullWidth = true,
  open,
  onClose,
  onCancel,
  onConfirm,
  titleText,
  children,
  contentText,
  closeText,
  confirmText,
  disabled = false,
}: DialogProps) => {
  const classes = useStyles();
  return (
    <MaterialDialog
      aria-labelledby='form-dialog-title'
      classes={{ paper: classes.paper }}
      fullWidth={fullWidth}
      maxWidth={maxWidth}
      onClose={onClose}
      open={open}>
      {titleText && <DialogTitle id='form-dialog-title'>{titleText}</DialogTitle>}
      {(contentText || children) && (
        <DialogContent>
          {contentText && <DialogContentText className={classes.contentText}>{contentText}</DialogContentText>}
          {children}
        </DialogContent>
      )}
      <DialogActions>
        <Button color='primary' onClick={onCancel || onClose} variant='text'>
          {closeText || 'Lukk'}
        </Button>
        {onConfirm && (
          <Button color='primary' disabled={disabled} onClick={onConfirm || onCancel} variant='text'>
            {confirmText || 'OK'}
          </Button>
        )}
      </DialogActions>
    </MaterialDialog>
  );
};

export default Dialog;

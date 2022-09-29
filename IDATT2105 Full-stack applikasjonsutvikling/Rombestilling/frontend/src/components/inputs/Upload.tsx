import { forwardRef, useState } from 'react';
import classnames from 'classnames';
import { UseFormReturn, UseFormRegisterReturn, UseFormSetValue, UseFormWatch } from 'react-hook-form';
import API from 'api/api';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI Components
import { ButtonProps, makeStyles, Button, FormHelperText, List, ListItem, ListItemText, ListItemSecondaryAction } from '@material-ui/core';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project components
import Paper from 'components/layout/Paper';
import VerifyDialog from 'components/layout/VerifyDialog';

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'grid',
    gridGap: theme.spacing(1),
  },
  button: {
    height: 50,
  },
  remove: {
    color: theme.palette.error.main,
  },
  img: {
    maxHeight: 150,
    maxWidth: '90%',
    borderRadius: theme.shape.borderRadius,
  },
  gutters: {
    margin: theme.spacing(1, 0, 2),
  },
}));

export type SingleImageUploadProps = ButtonProps &
  Pick<UseFormReturn, 'formState'> & {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    watch: UseFormWatch<any>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setValue: UseFormSetValue<any>;
    name: string;
    register: UseFormRegisterReturn;
    label?: string;
    gutters?: boolean;
  };

export const SingleImageUpload = forwardRef(
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  ({ register, watch, setValue, name, formState, label = 'Last opp bilde', gutters, ...props }: SingleImageUploadProps, ref) => {
    const classes = useStyles();
    const showSnackbar = useSnackbar();
    const image: string | undefined = watch(name);
    const [isLoading, setIsLoading] = useState(false);

    const upload = async (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        setIsLoading(true);
        try {
          const data = await API.uploadFile(file);
          setValue(name, data.data.display_url);
          showSnackbar('Bildet ble lagt til', 'info');
        } catch (e) {
          showSnackbar(e.detail, 'error');
        }
        setIsLoading(false);
      }
    };

    const removeImage = async () => {
      setValue(name, '');
    };

    return (
      <div className={classnames(classes.root, gutters && classes.gutters)}>
        {image ? (
          <List className={classes.root}>
            <ListItem component={Paper} noPadding>
              <img className={classes.img} src={image} />
              <ListItemSecondaryAction>
                <VerifyDialog iconButton onConfirm={removeImage} titleText='Fjern bilde'>
                  <DeleteIcon className={classes.remove} />
                </VerifyDialog>
              </ListItemSecondaryAction>
            </ListItem>
          </List>
        ) : (
          <div>
            <input hidden {...register} />
            <input accept='image/*' hidden id='file-upload-button' onChange={upload} type='file' />
            <label htmlFor='file-upload-button'>
              <Button className={classes.button} color='primary' component='span' disabled={isLoading} fullWidth variant='contained' {...props}>
                {label}
              </Button>
            </label>
          </div>
        )}
        {Boolean(formState.errors[name]) && <FormHelperText error>{formState.errors[name]?.message}</FormHelperText>}
      </div>
    );
  },
);

SingleImageUpload.displayName = 'SingleImageUpload';

export type SingleFileSelectProps = ButtonProps &
  Pick<UseFormReturn, 'formState'> & {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    watch: UseFormWatch<any>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setValue: UseFormSetValue<any>;
    name: string;
    register: UseFormRegisterReturn;
    label?: string;
    gutters?: boolean;
  };

export const SingleFileSelect = forwardRef(
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  ({ register, watch, setValue, name, formState, label = 'Last opp fil', gutters, ...props }: SingleFileSelectProps, ref) => {
    const classes = useStyles();
    const file: File | undefined = watch(name);

    const upload = (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        setValue(name, file);
      }
    };

    const removeFile = () => setValue(name, undefined);

    return (
      <div className={classnames(classes.root, gutters && classes.gutters)}>
        {file ? (
          <List className={classes.root}>
            <ListItem component={Paper} noPadding>
              <ListItemText primary={file.name} />
              <ListItemSecondaryAction>
                <VerifyDialog iconButton onConfirm={removeFile} titleText='Fjern fil'>
                  <DeleteIcon className={classes.remove} />
                </VerifyDialog>
              </ListItemSecondaryAction>
            </ListItem>
          </List>
        ) : (
          <div>
            <input hidden {...register} />
            <input accept='.csv' hidden id='csv-upload-button' onChange={upload} type='file' />
            <label htmlFor='csv-upload-button'>
              <Button className={classes.button} color='primary' component='span' fullWidth variant='contained' {...props}>
                {label}
              </Button>
            </label>
          </div>
        )}
        {Boolean(formState.errors[name]) && <FormHelperText error>{formState.errors[name]?.message}</FormHelperText>}
      </div>
    );
  },
);

SingleFileSelect.displayName = 'SingleFileUpload';

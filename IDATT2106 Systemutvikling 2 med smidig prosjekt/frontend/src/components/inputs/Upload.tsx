import { forwardRef, useState } from 'react';
import { UseFormReturn, UseFormRegisterReturn, UseFormSetValue, UseFormWatch } from 'react-hook-form';
import API from 'api/api';
import { useSnackbar } from 'hooks/Snackbar';
import { Activity } from 'types/Types';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { ButtonProps } from '@material-ui/core/Button';
import { Button, FormHelperText, List, ListItem, ListItemSecondaryAction } from '@material-ui/core';

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
}));

export type ImageUploadProps = ButtonProps &
  Pick<UseFormReturn, 'formState'> & {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    watch: UseFormWatch<any>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setValue: UseFormSetValue<any>;
    name: string;
    register: UseFormRegisterReturn;
    label?: string;
  };
// eslint-disable-next-line @typescript-eslint/no-unused-vars
export const ImageUpload = forwardRef(({ register, watch, setValue, name, formState, label = 'Last opp fil', ...props }: ImageUploadProps, ref) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const images: Activity['images'] | undefined = watch(name);
  const [isLoading, setIsLoading] = useState(false);

  const upload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files) {
      setIsLoading(true);
      try {
        const data = await Promise.all(Array.from(files).map((file) => API.uploadFile(file)));
        setValue(name, [
          ...(images || []),
          ...data.map((file) => ({
            url: file.data.display_url,
          })),
        ]);
        showSnackbar('Bildet ble lagt til', 'info');
      } catch (e) {
        showSnackbar(e.detail, 'error');
      }
      setIsLoading(false);
    }
  };

  const removeImage = async (i: number) => {
    if (images) {
      setValue(name, images.slice(0, i).concat(images.slice(i + 1, images.length)));
    }
  };

  return (
    <div className={classes.root}>
      <List className={classes.root}>
        {images?.map((image, i) => (
          <ListItem component={Paper} key={i} noPadding>
            <img className={classes.img} src={image.url} />
            <ListItemSecondaryAction>
              <VerifyDialog iconButton onConfirm={() => removeImage(i)} titleText='Fjern bilde'>
                <DeleteIcon className={classes.remove} />
              </VerifyDialog>
            </ListItemSecondaryAction>
          </ListItem>
        ))}
      </List>
      <div>
        <input hidden {...register} />
        <input accept='image/*' hidden id='file-upload-button' multiple onChange={upload} type='file' />
        <label htmlFor='file-upload-button'>
          <Button className={classes.button} color='primary' component='span' disabled={isLoading} fullWidth variant='contained' {...props}>
            {label}
          </Button>
        </label>
      </div>
      {Boolean(formState.errors[name]) && <FormHelperText error>{formState.errors[name]?.message}</FormHelperText>}
    </div>
  );
});

ImageUpload.displayName = 'ImageUpload';

export type SingleImageUploadProps = ButtonProps &
  Pick<UseFormReturn, 'formState'> & {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    watch: UseFormWatch<any>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setValue: UseFormSetValue<any>;
    name: string;
    register: UseFormRegisterReturn;
    label?: string;
  };

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export const SingleImageUpload = forwardRef(({ register, watch, setValue, name, formState, label = 'Last opp fil', ...props }: SingleImageUploadProps, ref) => {
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
    <div className={classes.root}>
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
});

SingleImageUpload.displayName = 'SingleImageUpload';

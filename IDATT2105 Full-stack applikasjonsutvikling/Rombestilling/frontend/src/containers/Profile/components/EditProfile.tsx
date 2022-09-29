import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { useSnackbar } from 'hooks/Snackbar';
import { useUpdateUser, useChangePassword, useLogout, useDeleteUser, useUser } from 'hooks/User';
import { User } from 'types/Types';
import { parseISO } from 'date-fns';
import { dateAsUTC, isUserAdmin } from 'utils';

// Material UI
import { makeStyles, Typography } from '@material-ui/core';

// Project components
import Paper from 'components/layout/Paper';
import VerifyDialog from 'components/layout/VerifyDialog';
import DatePicker from 'components/inputs/DatePicker';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import { Button } from '@material-ui/core';
import Dialog from 'components/layout/Dialog';
import { SingleImageUpload } from 'components/inputs/Upload';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  btnRow: {
    display: 'grid',
    gap: theme.spacing(2),
  },
  red: {
    color: theme.palette.error.main,
    borderColor: theme.palette.error.main,
    '&:hover': {
      borderColor: theme.palette.error.light,
    },
  },
}));

export type EditProfileProps = {
  user: User;
};

type UserEditData = Pick<User, 'firstName' | 'surname' | 'email' | 'image' | 'phoneNumber'> & {
  expirationDate: Date;
};

type ChangePasswordData = {
  oldPassword: string;
  newPassword: string;
  repeatNewPassword: string;
};

const EditProfile = ({ user }: EditProfileProps) => {
  const classes = useStyles();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const { data: signedInUser } = useUser();
  const { getValues, register: passwordRegister, formState: passwordFormState, handleSubmit: passwordHandleSubmit } = useForm<ChangePasswordData>();
  const updateUser = useUpdateUser();
  const changePassword = useChangePassword();
  const logout = useLogout();
  const deleteUser = useDeleteUser();
  const showSnackbar = useSnackbar();
  const { control, register, formState, handleSubmit, setValue, watch } = useForm<UserEditData>({
    defaultValues: {
      firstName: user.firstName,
      surname: user.surname,
      email: user.email,
      phoneNumber: user.phoneNumber,
      image: user.image,
      expirationDate: parseISO(user.expirationDate),
    },
  });
  const submit = async (data: UserEditData) => {
    updateUser.mutate(
      { userId: user.id, user: { ...data, expirationDate: dateAsUTC(data.expirationDate).toJSON() } },
      {
        onSuccess: () => {
          showSnackbar('Profilen ble oppdatert', 'success');
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );
  };

  const onChangePassword = async (data: ChangePasswordData) => {
    changePassword.mutate(
      { oldPassword: data.oldPassword, newPassword: data.newPassword },
      {
        onSuccess: () => {
          showSnackbar('Passordet ble oppdatert', 'success');
          setOpen(false);
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );
  };

  const confirmedDeleteUser = async () => {
    deleteUser.mutate(user.id, {
      onSuccess: () => {
        if (user.id === signedInUser?.id) {
          showSnackbar('Brukeren din ble slettet. Du vil nå bli sendt til forsiden.', 'success');
          setTimeout(() => logout(), 5000);
        } else {
          showSnackbar('Brukeren ble slettet. Du vil nå bli sendt til brukeroversikten.', 'success');
          setTimeout(() => navigate(URLS.USERS), 5000);
        }
      },
    });
  };

  return (
    <>
      <form className={classes.list} onSubmit={handleSubmit(submit)}>
        <Typography variant='h3'>Oppdater profil</Typography>
        <TextField
          disabled={updateUser.isLoading}
          formState={formState}
          label='Fornavn'
          {...register('firstName', { required: 'Feltet er påkrevd' })}
          required
        />
        <TextField
          disabled={updateUser.isLoading}
          formState={formState}
          label='Etternavn'
          {...register('surname', { required: 'Feltet er påkrevd' })}
          required
        />
        <TextField
          disabled={updateUser.isLoading}
          formState={formState}
          label='Epost'
          {...register('email', { required: 'Feltet er påkrevd' })}
          required
          type='email'
        />
        <TextField
          disabled={updateUser.isLoading}
          formState={formState}
          InputProps={{ type: 'number' }}
          label='Telefonnummer'
          {...register('phoneNumber', { required: 'Feltet er påkrevd' })}
          required
        />
        <SingleImageUpload
          formState={formState}
          label='Legg til bilde'
          name='image'
          register={register('image')}
          setValue={setValue}
          variant='outlined'
          watch={watch}
        />
        {isUserAdmin(signedInUser) && (
          <DatePicker control={control} disabled={updateUser.isLoading} formState={formState} fullWidth label='Aktiv til' name='expirationDate' type='date' />
        )}
        <div className={classes.btnRow}>
          <SubmitButton disabled={updateUser.isLoading} formState={formState}>
            Oppdater bruker
          </SubmitButton>
          <Button color='secondary' onClick={() => setOpen(true)} variant='outlined'>
            Endre passord
          </Button>
        </div>
        {isUserAdmin(signedInUser) && (
          <Paper className={classes.list}>
            <Typography variant='h3'>Faresone!</Typography>
            <VerifyDialog
              className={classes.red}
              closeText='Avbryt'
              confirmText='Slett brukeren'
              contentText='Sikker på at du vil slette brukeren? Du kan ikke angre dette. Reserveringer vil slettes og kan ikke gjenopprettes.'
              onConfirm={confirmedDeleteUser}>
              Slett bruker
            </VerifyDialog>
          </Paper>
        )}
      </form>
      <Dialog onClose={() => setOpen(false)} open={open} titleText='Endre Passord'>
        <form className={classes.list} onSubmit={passwordHandleSubmit(onChangePassword)}>
          <TextField
            disabled={updateUser.isLoading}
            formState={passwordFormState}
            label='Nåværende passord'
            type='password'
            {...passwordRegister('oldPassword', { required: 'Feltet er påkrevd' })}
            required
          />
          <TextField
            disabled={updateUser.isLoading}
            formState={passwordFormState}
            label='Nytt passord'
            type='password'
            {...passwordRegister('newPassword', { required: 'Feltet er påkrevd' })}
            required
          />
          <TextField
            disabled={updateUser.isLoading}
            formState={passwordFormState}
            label='Nytt passord'
            type='password'
            {...passwordRegister('repeatNewPassword', {
              required: 'Feltet er påkrevd',
              validate: {
                passwordEqual: (value) => value === getValues().newPassword || 'Passordene er ikke like',
              },
            })}
            required
          />
          <SubmitButton disabled={updateUser.isLoading} formState={passwordFormState}>
            Oppdater passord
          </SubmitButton>
        </form>
      </Dialog>
    </>
  );
};

export default EditProfile;

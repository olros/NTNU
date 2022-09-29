import { useForm } from 'react-hook-form';
import { useSnackbar } from 'hooks/Snackbar';
import { useUpdateUser, useChangePassword, useLogout, useDeleteUser } from 'hooks/User';
import { User } from 'types/Types';
import { TrainingLevel } from 'types/Enums';
import { traningLevelToText, dateAsUTC } from 'utils';
import { parseISO } from 'date-fns';
import { useState } from 'react';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import MenuItem from '@material-ui/core/MenuItem';

// Project components
import Paper from 'components/layout/Paper';
import VerifyDialog from 'components/layout/VerifyDialog';
import DatePicker from 'components/inputs/DatePicker';
import Select from 'components/inputs/Select';
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

type UserEditData = Pick<User, 'firstName' | 'surname' | 'email' | 'level' | 'image'> & {
  birthDate: Date | null;
};

type ChangePasswordData = {
  oldPassword: string;
  newPassword: string;
  repeatNewPassword: string;
};

const EditProfile = ({ user }: EditProfileProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const { getValues, register: passwordRegister, formState: passwordFormState, handleSubmit: passwordHandleSubmit } = useForm<ChangePasswordData>();
  const updateUser = useUpdateUser();
  const changePassword = useChangePassword();
  const logout = useLogout();
  const deleteUser = useDeleteUser();
  const showSnackbar = useSnackbar();
  const { register, control, formState, handleSubmit, setValue, watch } = useForm<UserEditData>({
    defaultValues: {
      firstName: user.firstName,
      surname: user.surname,
      email: user.email,
      level: user.level,
      birthDate: user.birthDate ? parseISO(user.birthDate) : null,
      image: user.image,
    },
  });
  const submit = async (data: UserEditData) => {
    updateUser.mutate(
      { userId: user.id, user: { ...data, birthDate: data.birthDate ? dateAsUTC(data.birthDate).toJSON() : null } },
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
    deleteUser.mutate(null, {
      onSuccess: () => {
        showSnackbar('Brukeren din ble slettet. Du vil nå bli sendt til forsiden.', 'success');
        setTimeout(() => logout(), 5000);
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
        <DatePicker control={control} dateProps={{ disableFuture: true }} formState={formState} label='Fødselsdato' name='birthDate' type='date' />
        <Select control={control} formState={formState} label='Trenings-nivå' name='level'>
          {Object.values(TrainingLevel).map((value, index) => (
            <MenuItem key={index} value={value}>
              {traningLevelToText(value as TrainingLevel)}
            </MenuItem>
          ))}
        </Select>
        <SingleImageUpload
          formState={formState}
          label='Legg til bilde'
          name='image'
          register={register('image')}
          setValue={setValue}
          variant='outlined'
          watch={watch}
        />
        <div className={classes.btnRow}>
          <SubmitButton disabled={updateUser.isLoading} formState={formState}>
            Oppdater bruker
          </SubmitButton>
          <Button color='secondary' onClick={() => setOpen(true)} variant='outlined'>
            Endre passord
          </Button>
        </div>
        <Paper className={classes.list}>
          <Typography variant='h3'>Faresone!</Typography>
          <VerifyDialog
            className={classes.red}
            closeText='Avbryt'
            confirmText='Slett brukeren min'
            contentText='Sikker på at du vil slette brukeren din? Du kan ikke angre dette. Dine innlegg, likes, kommentarer, påmeldinger, invitasjoner og brukeren din vil slettes. Aktiviteter du har opprettet vil bli værende, men vil bli stående uten eier.'
            onConfirm={confirmedDeleteUser}>
            Slett bruker
          </VerifyDialog>
        </Paper>
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

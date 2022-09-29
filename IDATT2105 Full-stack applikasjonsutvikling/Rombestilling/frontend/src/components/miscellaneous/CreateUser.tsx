import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { UserCreate } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useCreateUser, useBatchCreateUser } from 'hooks/User';
import { EMAIL_REGEX } from 'constant';
import { dateAsUTC } from 'utils';

// Material UI Components
import { makeStyles, Button, ButtonProps, Typography } from '@material-ui/core';

// Project components
import Expand from 'components/layout/Expand';
import Dialog from 'components/layout/Dialog';
import DatePicker from 'components/inputs/DatePicker';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import { SingleFileSelect } from 'components/inputs/Upload';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
}));

type FormValues = Omit<UserCreate, 'expirationDate'> & {
  expirationDate: Date | null;
};
type BatchFormValues = { file?: File };

const CreateUser = ({ children, ...props }: ButtonProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const showSnackbar = useSnackbar();
  const createUser = useCreateUser();
  const batchCreateUser = useBatchCreateUser();
  const { register, formState, handleSubmit, reset, control } = useForm<FormValues>();
  const {
    register: batchRegister,
    formState: batchFormState,
    handleSubmit: batchHandleSubmit,
    setValue: batchSetValue,
    watch: batchWatch,
    reset: batchReset,
  } = useForm<BatchFormValues>();

  const submit = async (data: FormValues) => {
    createUser.mutate(
      { ...data, expirationDate: data.expirationDate ? dateAsUTC(data.expirationDate).toJSON() : undefined },
      {
        onSuccess: () => {
          showSnackbar('Brukeren ble opprettet og har mottatt en epost med link for opprettelse av passord', 'success');
          setOpen(false);
          reset();
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );
  };

  const batchCreate = async (data: BatchFormValues) => {
    if (data.file) {
      batchCreateUser.mutate(data.file, {
        onSuccess: () => {
          showSnackbar('Brukerne ble opprettet og har mottatt en epost med link for opprettelse av passord', 'success');
          setOpen(false);
          batchReset();
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    } else {
      showSnackbar('Du må velge en CSV-fil', 'warning');
    }
  };

  return (
    <>
      <Button fullWidth variant='outlined' {...props} onClick={() => setOpen(true)}>
        {children}
      </Button>
      <Dialog onClose={() => setOpen(false)} open={open} titleText='Opprett bruker'>
        <div className={classes.grid}>
          <Expand primary='Opprett en bruker'>
            <form onSubmit={handleSubmit(submit)}>
              <TextField
                disabled={createUser.isLoading}
                formState={formState}
                label='Fornavn'
                {...register('firstName', { required: 'Feltet er påkrevd' })}
                required
              />
              <TextField
                disabled={createUser.isLoading}
                formState={formState}
                label='Etternavn'
                {...register('surname', { required: 'Feltet er påkrevd' })}
                required
              />
              <TextField
                disabled={createUser.isLoading}
                formState={formState}
                label='Epost'
                {...register('email', {
                  required: 'Feltet er påkrevd',
                  pattern: {
                    value: EMAIL_REGEX,
                    message: 'Ugyldig e-post',
                  },
                })}
                required
                type='email'
              />
              <TextField
                disabled={createUser.isLoading}
                formState={formState}
                InputProps={{ type: 'number' }}
                label='Telefonnummer'
                {...register('phoneNumber', { required: 'Feltet er påkrevd' })}
                required
              />
              <DatePicker
                control={control}
                disabled={createUser.isLoading}
                formState={batchFormState}
                fullWidth
                label='Aktiv til'
                name='expirationDate'
                type='date'
              />
              <SubmitButton disabled={createUser.isLoading} formState={formState}>
                Opprett bruker
              </SubmitButton>
            </form>
          </Expand>
          <Expand primary='Opprett flere brukere' secondary='Opprett flere brukere samtidig ved å laste opp en CSV-fil'>
            <form className={classes.grid} onSubmit={batchHandleSubmit(batchCreate)}>
              <Typography variant='subtitle2'>{`Last opp en CSV-fil med feltene: "firstName,surname,email,phoneNumber,expirationDate" i første rad`}</Typography>
              <SingleFileSelect
                disabled={batchCreateUser.isLoading}
                formState={batchFormState}
                label='Velg CSV-fil'
                name='file'
                register={batchRegister('file')}
                setValue={batchSetValue}
                variant='outlined'
                watch={batchWatch}
              />
              <SubmitButton disabled={batchCreateUser.isLoading} formState={batchFormState}>
                Opprett brukere
              </SubmitButton>
            </form>
          </Expand>
        </div>
      </Dialog>
    </>
  );
};

export default CreateUser;

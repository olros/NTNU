import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSnackbar } from 'hooks/Snackbar';
import { useCreateMembership, useBatchCreateMemberships } from 'hooks/Group';
import { EMAIL_REGEX } from 'constant';

// Material UI Components
import { makeStyles, Button, ButtonProps, Typography } from '@material-ui/core';

// Project components
import Expand from 'components/layout/Expand';
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import { SingleFileSelect } from 'components/inputs/Upload';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
}));

export type AddMembershipProps = ButtonProps & {
  groupId: string;
};

type FormValues = {
  email: string;
};
type BatchFormValues = { file?: File };

const AddMembership = ({ groupId, children, ...props }: AddMembershipProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const showSnackbar = useSnackbar();
  const createMembership = useCreateMembership(groupId);
  const batchCreateMemberships = useBatchCreateMemberships(groupId);
  const { register, formState, handleSubmit, reset } = useForm<FormValues>();
  const {
    register: batchRegister,
    formState: batchFormState,
    handleSubmit: batchHandleSubmit,
    setValue: batchSetValue,
    watch: batchWatch,
    reset: batchReset,
  } = useForm<BatchFormValues>();

  const submit = async (data: FormValues) => {
    createMembership.mutate(data.email, {
      onSuccess: () => {
        showSnackbar('Brukeren ble lagt til som medlem i gruppen', 'success');
        setOpen(false);
        reset();
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const batchCreate = async (data: BatchFormValues) => {
    if (data.file) {
      batchCreateMemberships.mutate(data.file, {
        onSuccess: () => {
          showSnackbar('Brukerne ble lagt til som medlemmer i gruppen', 'success');
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
      <Dialog onClose={() => setOpen(false)} open={open} titleText='Legg til medlem'>
        <div className={classes.grid}>
          <Expand primary='Legg til en bruker'>
            <form onSubmit={handleSubmit(submit)}>
              <TextField
                disabled={createMembership.isLoading}
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
              <SubmitButton disabled={createMembership.isLoading} formState={formState}>
                Legg til bruker
              </SubmitButton>
            </form>
          </Expand>
          <Expand primary='Legg til flere brukere' secondary='Legg til flere brukere samtidig ved å laste opp en CSV-fil'>
            <form className={classes.grid} onSubmit={batchHandleSubmit(batchCreate)}>
              <Typography variant='subtitle2'>{`Last opp en CSV-fil med feltet: "email" i første rad`}</Typography>
              <SingleFileSelect
                disabled={batchCreateMemberships.isLoading}
                formState={batchFormState}
                label='Velg CSV-fil'
                name='file'
                register={batchRegister('file')}
                setValue={batchSetValue}
                variant='outlined'
                watch={batchWatch}
              />
              <SubmitButton disabled={batchCreateMemberships.isLoading} formState={batchFormState}>
                Legg til brukere
              </SubmitButton>
            </form>
          </Expand>
        </div>
      </Dialog>
    </>
  );
};

export default AddMembership;

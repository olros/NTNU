import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { GroupCreate } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useCreateGroup } from 'hooks/Group';

// Material UI Components
import { Button, ButtonProps } from '@material-ui/core';

// Project components
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';

const CreateGroup = ({ children, ...props }: ButtonProps) => {
  const [open, setOpen] = useState(false);
  const showSnackbar = useSnackbar();
  const navigate = useNavigate();
  const createGroup = useCreateGroup();
  const { register, formState, handleSubmit, reset } = useForm<GroupCreate>();

  const submit = async (data: GroupCreate) => {
    createGroup.mutate(data, {
      onSuccess: (group) => {
        showSnackbar('Gruppen ble opprettet', 'success');
        setOpen(false);
        reset();
        navigate(`${URLS.GROUPS}${group.id}/`);
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  return (
    <>
      <Button fullWidth variant='outlined' {...props} onClick={() => setOpen(true)}>
        {children}
      </Button>
      <Dialog onClose={() => setOpen(false)} open={open} titleText='Opprett gruppe'>
        <form onSubmit={handleSubmit(submit)}>
          <TextField disabled={createGroup.isLoading} formState={formState} label='Navn' {...register('name', { required: 'Feltet er påkrevd' })} required />
          <SubmitButton disabled={createGroup.isLoading} formState={formState}>
            Opprett gruppe
          </SubmitButton>
        </form>
      </Dialog>
    </>
  );
};

export default CreateGroup;

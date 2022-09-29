import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { SectionCreate } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useCreateSection } from 'hooks/Section';

// Material UI Components
import { Button, ButtonProps } from '@material-ui/core';

// Project components
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import { SingleImageUpload } from 'components/inputs/Upload';

export type CreateRoomProps = ButtonProps & {
  parentId?: string;
};

const CreateRoom = ({ parentId, children, ...props }: CreateRoomProps) => {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const createSection = useCreateSection();
  const { formState, handleSubmit, register, watch, reset, setValue } = useForm<SectionCreate>();
  const submit = async (data: SectionCreate) => {
    await createSection.mutate(
      { ...data, parentId },
      {
        onSuccess: (data) => {
          showSnackbar(parentId ? 'Delen ble opprettet' : 'Rommet ble opprettet', 'success');
          setOpen(false);
          reset();
          navigate(`${URLS.ROOMS}${data.id}/`);
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );
  };

  const name = parentId ? 'del av rom' : 'rom';

  return (
    <>
      <Button fullWidth variant='outlined' {...props} onClick={() => setOpen(true)}>
        {children}
      </Button>
      <Dialog onClose={() => setOpen(false)} open={open} titleText={`Opprett ${name}`}>
        <form onSubmit={handleSubmit(submit)}>
          <TextField formState={formState} label='Navn' required {...register('name', { required: 'Du må oppgi et navn' })} />
          <TextField
            formState={formState}
            InputProps={{ type: 'number' }}
            label='Kapasitet'
            required
            {...register('capacity', { required: 'Du må oppgi en kapasitet' })}
          />
          <TextField formState={formState} label='Beskrivelse' maxRows={10} minRows={3} multiline {...register('description')} />
          <SingleImageUpload
            formState={formState}
            gutters
            label='Legg til bilde'
            name='image'
            register={register('image')}
            setValue={setValue}
            variant='outlined'
            watch={watch}
          />
          <SubmitButton formState={formState}>{`Opprett ${name}`}</SubmitButton>
        </form>
      </Dialog>
    </>
  );
};

export default CreateRoom;

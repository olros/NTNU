import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { Section } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useUpdateSection, useDeleteSection } from 'hooks/Section';

// Material UI Components
import { makeStyles, Button, ButtonProps } from '@material-ui/core';

// Project components
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import { SingleImageUpload } from 'components/inputs/Upload';
import VerifyDialog from 'components/layout/VerifyDialog';

const useStyles = makeStyles((theme) => ({
  red: {
    marginTop: theme.spacing(2),
    color: theme.palette.error.main,
    borderColor: theme.palette.error.main,
    '&:hover': {
      borderColor: theme.palette.error.light,
    },
  },
}));

type UpdateSection = Omit<Section, 'children' | 'parent'>;

export type EditRoomProps = ButtonProps & {
  room: UpdateSection;
  sectionType: 'room' | 'section';
};

const EditRoom = ({ room, sectionType, children, ...props }: EditRoomProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const updateSection = useUpdateSection(room.id);
  const deleteSection = useDeleteSection(room.id);
  const { formState, handleSubmit, register, watch, setValue } = useForm<UpdateSection>({ defaultValues: { ...room } });

  const submit = async (data: UpdateSection) => {
    await updateSection.mutate(
      { ...data },
      {
        onSuccess: () => {
          showSnackbar(sectionType === 'section' ? 'Delen ble oppdatert' : 'Rommet ble oppdatert', 'success');
          setOpen(false);
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );
  };

  const name = sectionType === 'section' ? 'del av rom' : 'rom';

  const confirmedDeleteSection = () => {
    deleteSection.mutate(null, {
      onSuccess: () => {
        showSnackbar(sectionType === 'section' ? 'Delen ble slettet' : 'Rommet ble slettet', 'success');
        navigate(URLS.ROOMS);
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
      <Dialog onClose={() => setOpen(false)} open={open} titleText={`Endre ${name}`}>
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
          <SubmitButton formState={formState}>{`Oppdater ${name}`}</SubmitButton>
          <VerifyDialog
            className={classes.red}
            closeText='Avbryt'
            confirmText={sectionType === 'section' ? 'Slett delen av rommen' : 'Slett rommet'}
            contentText='Sikker på at du vil slette dette? Du kan ikke angre dette. Reservasjonene vil slettes og kan ikke gjennopprettes.'
            onConfirm={confirmedDeleteSection}>
            {sectionType === 'section' ? 'Slett delen av rommen' : 'Slett rommet'}
          </VerifyDialog>
        </form>
      </Dialog>
    </>
  );
};

export default EditRoom;

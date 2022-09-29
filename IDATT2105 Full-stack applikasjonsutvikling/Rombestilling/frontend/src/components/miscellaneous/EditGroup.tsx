import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import URLS from 'URLS';
import { Group } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useUpdateGroup, useDeleteGroup } from 'hooks/Group';

// Material UI Components
import { makeStyles, Button, ButtonProps } from '@material-ui/core';

// Project components
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
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

export type EditGroupProps = ButtonProps & {
  group: Group;
};

const EditGroup = ({ group, children, ...props }: EditGroupProps) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const updateGroup = useUpdateGroup(group.id);
  const deleteGroup = useDeleteGroup(group.id);
  const { formState, handleSubmit, register } = useForm<Group>({ defaultValues: { ...group } });

  const submit = async (data: Group) => {
    await updateGroup.mutate(data, {
      onSuccess: () => {
        showSnackbar('Gruppen ble oppdatert', 'success');
        setOpen(false);
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const confirmedDeleteSection = () => {
    deleteGroup.mutate(null, {
      onSuccess: () => {
        showSnackbar('Gruppen ble slettet', 'success');
        navigate(URLS.GROUPS);
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
          <SubmitButton formState={formState}>{`Oppdater gruppen`}</SubmitButton>
          <VerifyDialog
            className={classes.red}
            closeText='Avbryt'
            confirmText='Slett gruppen'
            contentText='Sikker på at du vil slette gruppen? Du kan ikke angre dette. Reservasjonene vil slettes og kan ikke gjennopprettes.'
            onConfirm={confirmedDeleteSection}>
            Slett gruppen
          </VerifyDialog>
        </form>
      </Dialog>
    </>
  );
};

export default EditGroup;

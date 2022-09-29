import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import URLS from 'URLS';
import classnames from 'classnames';
import { formatDate, isUserAdmin } from 'utils';
import { parseISO } from 'date-fns';
import { Reservation } from 'types/Types';
import { useReservationById, useUpdateReservation, useDeleteReservation } from 'hooks/Reservation';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser } from 'hooks/User';

// Material UI Components
import { makeStyles, Button, ButtonProps, Typography } from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project components
import Dialog, { DialogProps } from 'components/layout/Dialog';
import VerifyDialog from 'components/layout/VerifyDialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  top: {
    gridTemplateColumns: '1fr auto',
  },
  header: {
    alignSelf: 'center',
    '& a': {
      color: theme.palette.text.primary,
    },
  },
  remove: {
    color: theme.palette.error.main,
  },
  about: {
    whiteSpace: 'break-spaces',
  },
}));

export type ReservationInfoProps = {
  sectionId: string;
  reservationId: string;
  onDelete?: () => void;
};

const ReservationInfo = ({ sectionId, reservationId, onDelete }: ReservationInfoProps) => {
  const classes = useStyles();
  const { data } = useReservationById(sectionId, reservationId);
  const { data: user } = useUser();
  const showSnackbar = useSnackbar();
  const deleteReservation = useDeleteReservation(sectionId, reservationId);
  const canEdit = useMemo(
    () => isUserAdmin(user) || Boolean((data?.type === 'user' && data.user.id === user?.id) || (data?.type === 'group' && data.group.isMember)),
    [data, user],
  );
  const removeReservation = async () =>
    deleteReservation.mutate(null, {
      onSuccess: () => {
        showSnackbar('Reservasjonen ble slettet', 'success');
        if (onDelete) {
          onDelete();
        }
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });

  if (!data) {
    return null;
  }

  const creator = data.type === 'group' ? data.group.name : `${data.user.firstName} ${data.user.surname}`;

  return (
    <div className={classes.grid}>
      <div className={classnames(classes.grid, classes.top)}>
        <Typography className={classes.header} variant='h3'>
          <Link to={`${URLS.ROOMS}${data.section.id}/`}>{data.section.name}</Link>
        </Typography>
        {canEdit && (
          <VerifyDialog iconButton onConfirm={removeReservation} titleText='Slett reservasjon'>
            <DeleteIcon className={classes.remove} />
          </VerifyDialog>
        )}
      </div>
      <Typography className={classes.about} variant='body1'>{`Fra: ${formatDate(parseISO(data.fromTime))}
Til: ${formatDate(parseISO(data.toTime))}
Personer: ${data.nrOfPeople}
${canEdit ? `Reservert av: ${creator}\n` : ''}Beskrivelse: ${data.text}`}</Typography>
      {canEdit && <ReservationEditDialog reservation={data} variant='text' />}
    </div>
  );
};

export default ReservationInfo;

export type ReservationEditDialogProps = ButtonProps & {
  reservation: Reservation;
};

export const ReservationEditDialog = ({ reservation, ...props }: ReservationEditDialogProps) => {
  const [open, setOpen] = useState(false);
  const showSnackbar = useSnackbar();
  const updateReservation = useUpdateReservation(reservation.section.id, reservation.id);
  const { formState, handleSubmit, register } = useForm<Pick<Reservation, 'text'>>({ defaultValues: { text: reservation.text } });
  const submit = async (data: Pick<Reservation, 'text'>) =>
    updateReservation.mutate(
      { ...data, type: reservation.type },
      {
        onSuccess: () => {
          showSnackbar('Reservasjonen ble oppdatert', 'success');
          setOpen(false);
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      },
    );

  return (
    <>
      <Button fullWidth variant='outlined' {...props} onClick={() => setOpen(true)}>
        Endre tekst
      </Button>
      <Dialog onClose={() => setOpen(false)} open={open} titleText={`Endre tekst`}>
        <form onSubmit={handleSubmit(submit)}>
          <TextField formState={formState} label='Beskrivelse' maxRows={5} minRows={2} multiline {...register('text')} />
          <SubmitButton formState={formState}>{`Endre tekst`}</SubmitButton>
        </form>
      </Dialog>
    </>
  );
};

export type ReservationInfoDialogProps = ReservationInfoProps & DialogProps;

export const ReservationInfoDialog = ({ reservationId, sectionId, onClose, onDelete, ...props }: ReservationInfoDialogProps) => (
  <Dialog onClose={() => onClose()} {...props}>
    <ReservationInfo onDelete={onDelete} reservationId={reservationId} sectionId={sectionId} />
  </Dialog>
);

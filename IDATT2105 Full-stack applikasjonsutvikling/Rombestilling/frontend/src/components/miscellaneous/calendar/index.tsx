import { useEffect, useMemo, useState, ReactNode } from 'react';
import { InfiniteData } from 'react-query';
import { Reservation, PaginationResponse } from 'types/Types';
import { useUserReservations, useSectionReservations, useGroupReservations } from 'hooks/Reservation';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser } from 'hooks/User';
import { parseISO, endOfWeek, startOfWeek, endOfDay, startOfDay, endOfMonth, startOfMonth, getHours, getMinutes, isAfter, isBefore, addMonths } from 'date-fns';
import { formatTime, isUserAdmin } from 'utils';
import { CALENDAR_INFO_DIALOG } from 'constant';
import { getCookie, setCookie } from 'api/cookie';
import { ViewState, AppointmentModel, EditingState, IntegratedEditing, ChangeSet, SchedulerDateTime } from '@devexpress/dx-react-scheduler';
import {
  Scheduler,
  MonthView,
  WeekView,
  DayView,
  Toolbar,
  ToolbarProps,
  DateNavigator,
  ViewSwitcher,
  Appointments,
  AppointmentForm,
  DragDropProvider,
} from '@devexpress/dx-react-scheduler-material-ui';

// Material-UI
import { Button, Typography, LinearProgress, makeStyles, SwipeableDrawer, Slide } from '@material-ui/core';

// Project components
import Dialog from 'components/layout/Dialog';
import Paper from 'components/layout/Paper';
import ReserveForm from 'components/miscellaneous/ReserveForm';
import { ReservationInfoDialog } from 'components/miscellaneous/ReservationInfo';
import Container from 'components/layout/Container';

// Styles
const useStyles = makeStyles((theme) => ({
  root: {
    marginBottom: theme.spacing(8),
    '& > div': {
      maxHeight: 600,
    },
    '& div:first-child': {
      whiteSpace: 'break-spaces',
    },
    '& table': {
      minWidth: 'unset',
    },
  },
  button: {
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
  },
  appointment: {
    color: theme.palette.get<string>({ light: theme.palette.common.black, dark: theme.palette.common.white }),
    background: theme.palette.background.paper,
    '&:hover': {
      background: `${theme.palette.background.paper}cc`,
    },
  },
  toolbarRoot: {
    position: 'relative',
  },
  progress: {
    position: 'absolute',
    width: '100%',
    bottom: 0,
    left: 0,
  },
  list: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  reservationPaper: {
    maxWidth: theme.breakpoints.values.md,
    margin: 'auto',
    padding: theme.spacing(3, 2, 5),
    borderRadius: `${theme.shape.borderRadius}px ${theme.shape.borderRadius}px 0 0`,
    background: theme.palette.background.paper,
  },
  fixedBottom: {
    position: 'fixed',
    bottom: 0,
    left: 0,
    right: 0,
    paddingBottom: theme.spacing(1),
    zIndex: 1,
    [theme.breakpoints.down('lg')]: {
      bottom: 70,
    },
  },
  text: {
    padding: theme.spacing(0, 1),
  },
}));

type Filters = {
  fromTimeAfter: string;
  toTimeBefore: string;
};

const DEFAULT_FILTERS: Filters = {
  fromTimeAfter: startOfWeek(new Date(), { weekStartsOn: 1 }).toJSON(),
  toTimeBefore: endOfWeek(new Date(), { weekStartsOn: 1 }).toJSON(),
};

export type UserCalendarProps = {
  userId?: string;
};

export const UserCalendar = ({ userId }: UserCalendarProps) => {
  const [filters, setFilters] = useState<Filters>(DEFAULT_FILTERS);
  const { data, isLoading } = useUserReservations(userId, filters);
  return <Calendar data={data} isLoading={isLoading} setFilters={setFilters} />;
};

export type GroupCalendarProps = {
  groupId: string;
};

export const GroupCalendar = ({ groupId }: GroupCalendarProps) => {
  const [filters, setFilters] = useState<Filters>(DEFAULT_FILTERS);
  const { data, isLoading } = useGroupReservations(groupId, filters);
  return <Calendar data={data} isLoading={isLoading} setFilters={setFilters} />;
};

export type SectionCalendarProps = {
  sectionId: string;
};

export const SectionCalendar = ({ sectionId }: SectionCalendarProps) => {
  const [filters, setFilters] = useState<Filters>(DEFAULT_FILTERS);
  const { data, isLoading } = useSectionReservations(sectionId, filters);
  return <Calendar data={data} isLoading={isLoading} sectionId={sectionId} setFilters={setFilters} />;
};

export type CalendarProps = {
  data?: InfiniteData<PaginationResponse<Reservation>>;
  setFilters: React.Dispatch<React.SetStateAction<Filters>>;
  isLoading: boolean;
  sectionId?: string;
};

type ViewTypes = 'Day' | 'Week' | 'Month';
type NewAppointmentType = { endDate: Date; startDate: Date };
const NEW_APPOINTMENT = { title: 'Ny reservasjon', id: 'new-appointment' };

const Calendar = ({ data, isLoading, setFilters, sectionId }: CalendarProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { data: user } = useUser();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [currentViewName, setCurrentViewName] = useState<ViewTypes>('Day');
  const [addedAppointment, setAddedAppointment] = useState<AppointmentModel | undefined>();
  const [reservationOpen, setReservationOpen] = useState(false);
  const [infoDialogOpen, setInfoDialogOpen] = useState(Boolean(sectionId && !getCookie(CALENDAR_INFO_DIALOG)));
  const reservations = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const displayedReservations = useMemo(
    () => [
      ...(addedAppointment ? [addedAppointment] : []),
      ...reservations.map(
        (reservation) => ({ ...reservation, startDate: reservation.fromTime, endDate: reservation.toTime, title: 'Reservert' } as AppointmentModel),
      ),
    ],
    [reservations, addedAppointment],
  );

  useEffect(() => {
    if (currentViewName === 'Day') {
      setFilters({ fromTimeAfter: startOfDay(currentDate).toJSON(), toTimeBefore: endOfDay(currentDate).toJSON() });
    } else if (currentViewName === 'Week') {
      setFilters({ fromTimeAfter: startOfWeek(currentDate, { weekStartsOn: 1 }).toJSON(), toTimeBefore: endOfWeek(currentDate, { weekStartsOn: 1 }).toJSON() });
    } else {
      setFilters({ fromTimeAfter: startOfMonth(currentDate).toJSON(), toTimeBefore: endOfMonth(currentDate).toJSON() });
    }
  }, [setFilters, currentViewName, currentDate]);

  const isTouchScreen = useMemo(() => matchMedia('(pointer: coarse)').matches, []);

  const stopReservation = () => {
    setReservationOpen(false);
    setAddedAppointment(undefined);
  };

  const schedulerDateTimeToDate = (time: SchedulerDateTime) => {
    if (time instanceof Date) {
      return time;
    } else if (typeof time === 'number') {
      return new Date(time);
    } else {
      return parseISO(time);
    }
  };

  type AppointmentProps = {
    data: AppointmentModel;
  };

  const Appointment = ({ data }: AppointmentProps) => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <Appointments.Appointment className={classes.appointment} data={data} draggable={false} onClick={() => setOpen(true)} resources={[]}>
          <Typography color='inherit' variant='caption'>
            {data.title}
          </Typography>
          <br />
          <Typography color='inherit' variant='caption'>
            {`${formatTime(schedulerDateTimeToDate(data.startDate))} - ${formatTime(schedulerDateTimeToDate(data.endDate))}`}
          </Typography>
          <br />
          <Typography color='inherit' variant='caption'>
            {data.text}
          </Typography>
        </Appointments.Appointment>
        {open && data.id && data.id !== NEW_APPOINTMENT.id && (
          <ReservationInfoDialog onClose={() => setOpen(false)} open={open} reservationId={String(data.id)} sectionId={String(data.section.id)} />
        )}
      </>
    );
  };

  const DayViewTableCell = ({ onDoubleClick, ...restProps }: DayView.TimeTableCellProps) => (
    <DayView.TimeTableCell {...(isTouchScreen ? { onClick: onDoubleClick } : { onDoubleClick })} {...restProps} />
  );
  const WeekViewTableCell = ({ onDoubleClick, ...restProps }: WeekView.TimeTableCellProps) => (
    <WeekView.TimeTableCell {...(isTouchScreen ? { onClick: onDoubleClick } : { onDoubleClick })} {...restProps} />
  );

  type ToolbarWithLoadingProps = ToolbarProps & {
    children?: ReactNode;
  };

  const ToolbarWithLoading = ({ children, ...restProps }: ToolbarWithLoadingProps) => (
    <div className={classes.toolbarRoot}>
      <Toolbar.Root {...restProps}>{children}</Toolbar.Root>
      {isLoading && <LinearProgress className={classes.progress} />}
    </div>
  );

  const commitChanges = ({ changed }: ChangeSet) => {
    if (changed) {
      addAppointment(changed[NEW_APPOINTMENT.id]);
    }
  };

  const addAppointment = (newAppointment: NewAppointmentType) => {
    if (!sectionId) {
      return;
    }
    if (isOutsideValidDates(newAppointment)) {
      showSnackbar(`Tiden må være i fremtiden og maks ${isUserAdmin(user) ? '6' : '1'} måned frem i tid`, 'warning');
    } else if (isOutsideValidTime(newAppointment)) {
      showSnackbar('Tiden må være mellom kl 06.00 og kl 20.00', 'warning');
    } else if (isOverlap(newAppointment)) {
      showSnackbar('Du kan ikke reservere en tid som overlapper med en annen tid', 'warning');
    } else {
      setAddedAppointment({ ...newAppointment, ...NEW_APPOINTMENT } as AppointmentModel);
    }
  };

  const isOutsideValidDates = (appointment: NewAppointmentType) =>
    isBefore(appointment.startDate, new Date()) || isAfter(appointment.endDate, addMonths(appointment.endDate, isUserAdmin(user) ? 6 : 1));

  const isOutsideValidTime = (appointment: NewAppointmentType) =>
    getHours(appointment.startDate) < 6 || (getHours(appointment.endDate) >= 20 && getMinutes(appointment.endDate) > 0);

  const isOverlap = (appointment: NewAppointmentType) =>
    reservations.some((reservation) => parseISO(reservation.fromTime) < appointment.endDate && parseISO(reservation.toTime) > appointment.startDate);

  const ReactiveCalendar = () => (
    <Scheduler data={displayedReservations} firstDayOfWeek={1} locale='no-NB'>
      <ViewState
        currentDate={currentDate}
        currentViewName={currentViewName}
        onCurrentDateChange={setCurrentDate}
        onCurrentViewNameChange={(newView) => setCurrentViewName(newView as ViewTypes)}
      />
      <DayView cellDuration={60} endDayHour={20} startDayHour={6} timeTableCellComponent={DayViewTableCell} />
      <WeekView endDayHour={20} startDayHour={6} timeTableCellComponent={WeekViewTableCell} />
      <MonthView />
      <Toolbar rootComponent={ToolbarWithLoading} />
      <ViewSwitcher />
      <DateNavigator
        openButtonComponent={({ text, onVisibilityToggle }) => (
          <Button className={classes.button} onClick={onVisibilityToggle} variant='text'>
            {text}
          </Button>
        )}
      />
      <EditingState
        addedAppointment={addedAppointment}
        onAddedAppointmentChange={(e) => addAppointment(e as NewAppointmentType)}
        onCommitChanges={commitChanges}
      />
      <IntegratedEditing />
      <Appointments appointmentComponent={Appointment} />
      <DragDropProvider
        allowDrag={(appointment) => Boolean(sectionId) && appointment.id === NEW_APPOINTMENT.id}
        allowResize={(appointment) => Boolean(sectionId) && appointment.id === NEW_APPOINTMENT.id}
      />
      {currentViewName !== 'Month' && <AppointmentForm visible={false} />}
    </Scheduler>
  );

  const INFO_TEXT = `For å reservere et rom ved hjelp av kalenderen så kan du ${
    isTouchScreen ? 'klikke' : 'dobbelklikke'
  } på et tidspunkt. Deretter kan du enten dra hele boksen som kommer opp for å flytte hele reservasjonen, eller dra i en av endene for å gjøre den lengre eller kortere.

Det er kun mulig å reservere når du ser på en dag eller en uke i kalenderen.

${isUserAdmin(user) ? 'Siden du er administrator' : 'Som bruker'} kan du bestille opp til ${
    isUserAdmin(user) ? '6 måneder' : '1 måned'
  } frem i tid. Reservasjon er kun mulig mellom kl 06.00 og 20.00.`;

  const acceptInfoDialog = () => {
    setInfoDialogOpen(false);
    setCookie(CALENDAR_INFO_DIALOG, 'true', 1000 * 3600 * 24 * 365);
  };

  return (
    <Paper className={classes.root} noPadding>
      {infoDialogOpen && (
        <Dialog
          confirmText='Ikke vis igjen'
          contentText={INFO_TEXT}
          onClose={() => setInfoDialogOpen(false)}
          onConfirm={acceptInfoDialog}
          open={infoDialogOpen}
          titleText='Reserver rom'
        />
      )}
      {sectionId && currentViewName !== 'Month' && (
        <Typography className={classes.text} variant='subtitle2'>
          {`${isTouchScreen ? 'Klikk' : 'Dobbelklikk'} på et tidspunkt for å opprette en reservasjon, dra på reservasjonen for å endre tiden.`}
        </Typography>
      )}
      <ReactiveCalendar />
      <Slide direction='up' in={Boolean(addedAppointment)}>
        <Container className={classes.fixedBottom} maxWidth='md'>
          <Button fullWidth onClick={() => setReservationOpen(true)}>
            Reserver
          </Button>
        </Container>
      </Slide>
      {sectionId && (
        <SwipeableDrawer
          anchor='bottom'
          classes={{ paper: classes.reservationPaper }}
          disableSwipeToOpen
          onClose={() => setReservationOpen(false)}
          onOpen={() => setReservationOpen(true)}
          open={reservationOpen}
          swipeAreaWidth={56}>
          <div className={classes.list}>
            {addedAppointment && (
              <ReserveForm
                from={schedulerDateTimeToDate(addedAppointment.startDate).toJSON()}
                onConfirm={stopReservation}
                sectionId={sectionId}
                to={schedulerDateTimeToDate(addedAppointment.endDate).toJSON()}
              />
            )}
            <Button onClick={stopReservation} variant='text'>
              Avbryt
            </Button>
          </div>
        </SwipeableDrawer>
      )}
    </Paper>
  );
};

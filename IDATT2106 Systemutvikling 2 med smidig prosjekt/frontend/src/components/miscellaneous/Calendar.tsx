import { useMemo } from 'react';
import { ActivityList } from 'types/Types';
import URLS from 'URLS';
import { Link } from 'react-router-dom';
import { urlEncode } from 'utils';
import { ViewState, AppointmentModel } from '@devexpress/dx-react-scheduler';
import { Scheduler, MonthView, Toolbar, DateNavigator, Appointments } from '@devexpress/dx-react-scheduler-material-ui';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import { Button, Typography } from '@material-ui/core';

// Project components
import Paper from 'components/layout/Paper';

// Styles
const useStyles = makeStyles((theme) => ({
  root: {
    '& div:first-child': {
      overflowY: 'hidden',
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
      background: `${theme.palette.background.paper}aa`,
    },
  },
}));

export type CalendarProps = {
  activities: Array<ActivityList>;
};

const Calendar = ({ activities }: CalendarProps) => {
  const classes = useStyles();
  const displayedActivities = useMemo(() => activities.map((activity) => activity as AppointmentModel), [activities]);

  type AppointmentProps = {
    data: AppointmentModel;
  };

  const Appointment = ({ data }: AppointmentProps) => {
    return (
      <Link to={`${URLS.ACTIVITIES}${data.id}/${urlEncode(data.title)}/`}>
        <Appointments.Appointment className={classes.appointment} data={data} draggable={false} resources={[]}>
          <Typography color='inherit' variant='caption'>
            {data.title}
          </Typography>
        </Appointments.Appointment>
      </Link>
    );
  };

  return (
    <Paper className={classes.root} noPadding>
      <Scheduler data={displayedActivities} firstDayOfWeek={1} locale='no-NB'>
        <ViewState />
        <MonthView />
        <Toolbar />
        <DateNavigator
          openButtonComponent={({ text, onVisibilityToggle }) => (
            <Button className={classes.button} onClick={onVisibilityToggle} variant='text'>
              {text}
            </Button>
          )}
        />
        <Appointments appointmentComponent={Appointment} />
      </Scheduler>
    </Paper>
  );
};

export default Calendar;

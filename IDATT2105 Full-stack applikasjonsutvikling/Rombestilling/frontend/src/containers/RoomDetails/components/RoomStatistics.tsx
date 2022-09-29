import { useMemo, ReactNode } from 'react';
import classnames from 'classnames';
import { useForm } from 'react-hook-form';
import { useSectionStatisticsById } from 'hooks/Section';
import { startOfMonth, endOfMonth } from 'date-fns';

// Material UI Components
import { makeStyles, Typography, Skeleton } from '@material-ui/core';

// Project Components
import Paper from 'components/layout/Paper';
import DatePicker from 'components/inputs/DatePicker';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  content: {
    gridTemplateColumns: 'repeat(2, 1fr)',
  },
  skeleton: {
    margin: 'auto',
  },
}));

export type SectionStatisticsProps = {
  sectionId: string;
};

type StatisticsFilter = {
  from: Date;
  to: Date;
};

const DEFAULT_FILTERS: StatisticsFilter = {
  from: startOfMonth(new Date()),
  to: endOfMonth(new Date()),
};

const SectionStatistics = ({ sectionId }: SectionStatisticsProps) => {
  const classes = useStyles();
  const { control, watch, formState } = useForm<StatisticsFilter>({ defaultValues: DEFAULT_FILTERS });
  const from = watch('from');
  const to = watch('to');
  const filters = useMemo(() => {
    try {
      return {
        fromTimeAfter: from.toISOString(),
        endTimeBefore: to.toISOString(),
      };
    } catch (e) {
      return {
        fromTimeAfter: DEFAULT_FILTERS.from.toISOString(),
        endTimeBefore: DEFAULT_FILTERS.to.toISOString(),
      };
    }
  }, [from, to]);
  const { data, isLoading, error } = useSectionStatisticsById(sectionId, filters);

  type StatisticProps = {
    label: string | ReactNode;
    result: string | number | ReactNode;
  };
  const Statistic = ({ label, result }: StatisticProps) => (
    <Paper className={classes.grid}>
      <Typography align='center' variant='h2'>
        {result}
      </Typography>
      <Typography align='center' variant='subtitle1'>
        {label}
      </Typography>
    </Paper>
  );

  return (
    <div>
      <div className={classnames(classes.grid, classes.content)}>
        <DatePicker control={control} dateProps={{ maxDate: to }} formState={formState} fullWidth label='Fra' name='from' type='date' />
        <DatePicker control={control} dateProps={{ minDate: from }} formState={formState} fullWidth label='Til' name='to' type='date' />
      </div>
      <div className={classnames(classes.grid, classes.content)}>
        {data ? (
          <>
            <Statistic label='Antall reservasjoner' result={data.nrOfReservation} />
            <Statistic label='Dager med reservasjon' result={data.daysWithReservation} />
            <Statistic label='Antall unike brukere' result={data.userReservationCount} />
            <Statistic label='Timer reservert' result={data.hoursOfReservation} />
          </>
        ) : isLoading ? (
          <>
            <Statistic label={<Skeleton className={classes.skeleton} width='60%' />} result={<Skeleton className={classes.skeleton} width='15%' />} />
            <Statistic label={<Skeleton className={classes.skeleton} width='60%' />} result={<Skeleton className={classes.skeleton} width='15%' />} />
            <Statistic label={<Skeleton className={classes.skeleton} width='60%' />} result={<Skeleton className={classes.skeleton} width='15%' />} />
            <Statistic label={<Skeleton className={classes.skeleton} width='60%' />} result={<Skeleton className={classes.skeleton} width='15%' />} />
          </>
        ) : (
          error && (
            <NotFoundIndicator header={error?.message || 'Det er ingen reservasjoner på dette rommet'} subtitle='Vi kunne dermed ikke generere statistikk' />
          )
        )}
      </div>
    </div>
  );
};

export default SectionStatistics;

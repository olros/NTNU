import { useMemo, useState } from 'react';
import classnames from 'classnames';
import { useForm } from 'react-hook-form';
import { formatDate } from 'utils';
import { parseISO, addMonths, getHours } from 'date-fns';
import { useUser } from 'hooks/User';
import { isUserAdmin } from 'utils';

// Material UI Components
import { makeStyles, Divider, useMediaQuery, Theme, Collapse, Button, Typography, FormControlLabel } from '@material-ui/core';

// Project Components
import DatePicker from 'components/inputs/DatePicker';
import TextField from 'components/inputs/TextField';
import Switch from 'components/inputs/Switch';
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';
import { RoomFilters } from 'containers/Rooms';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    margin: theme.spacing(2, 0),
  },
  filter: {
    display: 'grid',
    gap: theme.spacing(0.5),
    gridTemplateColumns: '2fr auto 3fr auto 3fr auto 140px',
    [theme.breakpoints.down('md')]: {
      gridTemplateColumns: '1fr',
    },
  },
  fewFilters: {
    gridTemplateColumns: '1fr auto 140px',
  },
  submit: {
    minHeight: '100%',
  },
}));

export type RoomFilterBoxProps = {
  updateFilters: (newFilters: RoomFilters) => void;
  defaultFilters: Required<RoomFilters>;
  filters: RoomFilters;
};
type FormValues = Pick<RoomFilters, 'name'> & {
  from: Date;
  to: Date;
};

const RoomFilterBox = ({ defaultFilters, filters, updateFilters }: RoomFilterBoxProps) => {
  const classes = useStyles();
  const { data: user } = useUser();
  const [isOpen, setIsOpen] = useState(true);
  const [fewFilters, setFewFilters] = useState(false);
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down('md'));
  const { control, watch, formState, handleSubmit, register, reset, setError } = useForm<FormValues>({
    defaultValues: { from: parseISO(defaultFilters.from), to: parseISO(defaultFilters.to) },
  });
  const fromDate = watch('from');
  const submit = async (data: FormValues) => {
    if (fewFilters) {
      updateFilters({ name: data.name });
    } else {
      if (data.to < data.from) {
        setError('to', { message: `"Til" må være etter "Fra"` });
        return;
      }
      updateFilters({ ...data, to: data.to.toJSON(), from: data.from.toJSON() });
    }
    setIsOpen(false);
  };
  const resetFilters = async () => {
    setIsOpen(true);
    reset();
    updateFilters(defaultFilters);
  };
  const FROM_DATE_RULES = useMemo(
    () => ({
      minDate: new Date(),
      maxDate: addMonths(new Date(), isUserAdmin(user) ? 6 : 1),
      minTime: new Date(0, 0, 0, 6),
      maxTime: new Date(0, 0, 0, 20),
    }),
    [user],
  );
  const TO_DATE_RULES = useMemo(
    () => ({
      minDate: fromDate,
      maxDate: fromDate,
      minTime: new Date(0, 0, 0, getHours(fromDate)),
      maxTime: new Date(0, 0, 0, 20),
    }),
    [fromDate, user],
  );

  return (
    <Paper blurred border className={classes.paper}>
      <Collapse in={!isOpen}>
        {filters && (
          <>
            {Boolean(filters.name?.length) && <Typography>{`Søkeord: "${filters.name}"`}</Typography>}
            {filters.from && <Typography>{`Fra ${formatDate(parseISO(filters.from))}`}</Typography>}
            {filters.to && <Typography>{`Til ${formatDate(parseISO(filters.to))}`}</Typography>}
          </>
        )}
        <Button fullWidth onClick={() => setIsOpen(true)} variant='text'>
          Endre søk
        </Button>
        <Button color='secondary' fullWidth onClick={resetFilters} variant='text'>
          Nullstill
        </Button>
      </Collapse>
      <Collapse in={isOpen}>
        <FormControlLabel
          control={<Switch color='primary' />}
          label='Vis opptatte rom'
          onChange={(e, isChecked) => setFewFilters(isChecked)}
          value={fewFilters}
        />
        <form className={classnames(classes.filter, fewFilters && classes.fewFilters)} onSubmit={handleSubmit(submit)}>
          <TextField formState={formState} label='Navn' margin='dense' noDefaultShrink noOutline {...register('name')} />
          {!fewFilters && (
            <>
              <Divider orientation={mdDown ? 'horizontal' : 'vertical'} />
              <DatePicker
                control={control}
                dateProps={FROM_DATE_RULES}
                formState={formState}
                label='Fra'
                margin='dense'
                name='from'
                noOutline
                required
                rules={{ required: 'Du må oppgi en fra-dato' }}
                type='date-time'
              />
              <Divider orientation={mdDown ? 'horizontal' : 'vertical'} />
              <DatePicker
                control={control}
                dateProps={TO_DATE_RULES}
                formState={formState}
                label='Til'
                margin='dense'
                name='to'
                noOutline
                required
                rules={{ required: 'Du må oppgi en til-dato' }}
                type='date-time'
              />
            </>
          )}
          <Divider orientation={mdDown ? 'horizontal' : 'vertical'} />
          <div>
            <SubmitButton className={classes.submit} formState={formState} noFeedback variant='text'>
              Søk
            </SubmitButton>
          </div>
        </form>
      </Collapse>
    </Paper>
  );
};

export default RoomFilterBox;

import { useState } from 'react';
import { useForm, Path, UnpackNestedValue } from 'react-hook-form';

// Material UI Components
import { makeStyles, Divider, useMediaQuery, Theme, Collapse, Button, Typography } from '@material-ui/core';

// Project Components
import TextField from 'components/inputs/TextField';
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(2),
    margin: theme.spacing(2, 0),
  },
  filter: {
    display: 'grid',
    gap: theme.spacing(0.5),
    gridTemplateColumns: '1fr auto 130px',
    [theme.breakpoints.down('md')]: {
      gridTemplateColumns: '1fr',
    },
  },
  submit: {
    minHeight: '100%',
  },
}));

export type UsersFilterBoxProps<Filters> = {
  updateFilters: (newFilters: Partial<Filters>) => void;
  filters: Partial<Filters>;
  field: keyof Filters;
  label: string;
};

// eslint-disable-next-line comma-spacing
const FilterBox = <Filters,>({ filters, updateFilters, field, label }: UsersFilterBoxProps<Filters>) => {
  const classes = useStyles();
  const [isOpen, setIsOpen] = useState(true);
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down('md'));
  const { formState, handleSubmit, register, reset } = useForm<Filters>();
  const submit = async (data: UnpackNestedValue<Filters>) => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    updateFilters({ ...data });
    setIsOpen(false);
  };
  const resetFilters = async () => {
    setIsOpen(true);
    reset();
    updateFilters({});
  };
  return (
    <Paper blurred border className={classes.paper}>
      <Collapse in={!isOpen}>
        {filters && <>{Boolean(filters[field]) && <Typography>{`Søkeord: "${filters[field]}"`}</Typography>}</>}
        <Button fullWidth onClick={() => setIsOpen(true)} variant='text'>
          Endre søk
        </Button>
        <Button color='secondary' fullWidth onClick={resetFilters} variant='text'>
          Nullstill
        </Button>
      </Collapse>
      <Collapse in={isOpen}>
        <form className={classes.filter} onSubmit={handleSubmit(submit)}>
          <TextField formState={formState} label={label} margin='dense' noDefaultShrink noOutline {...register((field as unknown) as Path<Filters>)} />
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

export default FilterBox;

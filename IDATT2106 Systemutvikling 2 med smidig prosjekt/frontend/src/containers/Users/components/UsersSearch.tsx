import { forwardRef, useState } from 'react';
import classnames from 'classnames';
import { useForm } from 'react-hook-form';
import { User } from 'types/Types';
import { TrainingLevel } from 'types/Enums';
import { traningLevelToText } from 'utils';

// Material UI Components
import { makeStyles, Theme } from '@material-ui/core/styles';
import { InputBaseProps } from '@material-ui/core/InputBase';
import { Button, MenuItem, IconButton, InputBase, Collapse, Divider, Hidden, Typography, useMediaQuery } from '@material-ui/core';

// Icons
import FilterIcon from '@material-ui/icons/TuneRounded';
import SearchIcon from '@material-ui/icons/SearchRounded';

// Project components
import { UsersFilters } from 'containers/Users';
import Select from 'components/inputs/Select';
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';
import Bool from 'components/inputs/Bool';

const useStyles = makeStyles((theme) => ({
  paper: {
    padding: theme.spacing(0.25, 0.5),
    overflow: 'hidden',
  },
  root: {
    display: 'flex',
    alignItems: 'center',
    width: '100%',
  },
  input: {
    marginLeft: theme.spacing(1),
    flex: 1,
  },
  iconButton: {
    padding: theme.spacing(1),
  },
  filterPaper: {
    padding: theme.spacing(1),
  },
  level: {
    display: 'flex',
    flexDirection: 'column',
  },
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  row: {
    gridTemplateColumns: 'auto 1fr',
    marginBottom: theme.spacing(1),
  },
  margin: {
    margin: 'auto 0',
  },
}));

type FormValues = {
  name?: string;
  level?: User['level'] | '';
  enableTrainingLevel: boolean;
  sort: string;
};

export type UsersSearchProps = {
  updateFilters: (newFilters: UsersFilters) => void;
};

const SORT_OPTIONS = [
  { name: 'Navn - A-Å', key: 'firstName,surname,ASC' },
  { name: 'Navn - Å-A', key: 'firstName,surname,DESC' },
];

const UsersSearch = ({ updateFilters }: UsersSearchProps) => {
  const classes = useStyles();
  const xlUp = useMediaQuery((theme: Theme) => theme.breakpoints.up('xl'));
  const [open, setOpen] = useState(false);
  const { reset, register, handleSubmit, control, formState } = useForm<FormValues>();

  const submit = async (data: FormValues) => {
    setOpen(false);
    const filters: UsersFilters = {};
    filters.sort = data.sort;
    if (data.name) {
      filters.search = data.name;
    }
    if (data.level && data.enableTrainingLevel) {
      filters['trainingLevel.level'] = data.level;
    }
    updateFilters(filters);
  };

  const resetFilters = async () => {
    setOpen(false);
    reset({
      name: '',
      level: '',
      enableTrainingLevel: false,
      sort: SORT_OPTIONS[0].key,
    });
    updateFilters({});
  };

  const Input = forwardRef(({ ...props }: InputBaseProps, ref) => (
    <InputBase {...props} className={classes.input} inputRef={ref} name={props.name} placeholder='Søk etter brukere' />
  ));
  Input.displayName = 'Input';

  return (
    <Paper className={classes.paper} noPadding>
      <form onSubmit={handleSubmit(submit)}>
        <div className={classes.root}>
          <Hidden xlUp>
            <IconButton aria-label='Filter eny' className={classes.iconButton} onClick={() => setOpen((prev) => !prev)}>
              <FilterIcon />
            </IconButton>
          </Hidden>
          <Input {...register('name')} />
          <IconButton aria-label='Søk' className={classes.iconButton} type='submit'>
            <SearchIcon />
          </IconButton>
        </div>
        <Collapse in={open || xlUp}>
          <Divider />
          <div className={classes.filterPaper}>
            <Select control={control} defaultValue={SORT_OPTIONS[0].key} formState={formState} label='Sortering' margin='dense' name='sort'>
              {SORT_OPTIONS.map((value) => (
                <MenuItem key={value.key} value={value.key}>
                  {value.name}
                </MenuItem>
              ))}
            </Select>
            <Typography variant='caption'>Treningsnivå</Typography>
            <div className={classnames(classes.grid, classes.row)}>
              <Bool className={classes.margin} control={control} formState={formState} name='enableTrainingLevel' type='checkbox' />
              <Select control={control} formState={formState} label='Trenings-nivå' margin='dense' name='level'>
                {Object.values(TrainingLevel).map((value, index) => (
                  <MenuItem key={index} value={value}>
                    {traningLevelToText(value as TrainingLevel)}
                  </MenuItem>
                ))}
              </Select>
            </div>
            <div className={classes.grid}>
              <SubmitButton formState={formState}>Aktiver filtre</SubmitButton>
              <Button onClick={resetFilters} variant='outlined'>
                Nullstill filtre
              </Button>
            </div>
          </div>
        </Collapse>
      </form>
    </Paper>
  );
};

export default UsersSearch;

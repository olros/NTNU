import { forwardRef, useCallback, useState } from 'react';
import classnames from 'classnames';
import { useForm } from 'react-hook-form';
import { Activity, LatLng } from 'types/Types';
import { TrainingLevel } from 'types/Enums';
import { traningLevelToText } from 'utils';
import { useMaps } from 'hooks/Utils';
import { Circle, Autocomplete, Data } from '@react-google-maps/api';

// Material UI Components
import { makeStyles, Theme } from '@material-ui/core/styles';
import { InputBaseProps } from '@material-ui/core/InputBase';
import { Button, MenuItem, IconButton, InputBase, Collapse, Divider, TextField, Hidden, Typography, useMediaQuery } from '@material-ui/core';

// Icons
import FilterIcon from '@material-ui/icons/TuneRounded';
import SearchIcon from '@material-ui/icons/SearchRounded';

// Project components
import { ActivityFilters } from 'containers/Activities';
import DatePicker from 'components/inputs/DatePicker';
import Select from 'components/inputs/Select';
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';
import Slider from 'components/inputs/Slider';
import Bool from 'components/inputs/Bool';
import GoogleMap from 'components/miscellaneous/GoogleMap';

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
  },
  margin: {
    margin: 'auto 0',
  },
  mapContainerStyle: {
    height: 300,
  },
  mapFilter: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  mapSearch: {
    marginBottom: theme.spacing(1),
  },
}));

type FormValues = {
  title?: string;
  endDate?: Date;
  startDate?: Date;
  level?: Activity['level'] | '';
  radius?: number;
  enableStartDate: boolean;
  enableEndDate: boolean;
  enableTrainingLevel: boolean;
  enableGeoLocation: boolean;
  sort: string;
};

export type ActivitiesSearchProps = {
  updateFilters: (newFilters: ActivityFilters) => void;
};

const SORT_OPTIONS = [
  { name: 'Tid - stigende', key: 'startDate,ASC' },
  { name: 'Tid - synkende', key: 'startDate,DESC' },
  { name: 'Tittel - A-Å', key: 'title,ASC' },
  { name: 'Tittel - Å-A', key: 'title,DESC' },
];

const ActivitiesSearch = ({ updateFilters }: ActivitiesSearchProps) => {
  const classes = useStyles();
  const xlUp = useMediaQuery((theme: Theme) => theme.breakpoints.up('xl'));
  const [open, setOpen] = useState(false);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [autocomplete, setAutocomplete] = useState<any>(null);
  const [location, setLocation] = useState<LatLng>({
    lat: 63.418022,
    lng: 10.402602,
  });
  const { watch, reset, register, handleSubmit, control, formState } = useForm<FormValues>();
  const radius = watch('radius');
  const { isLoaded: isMapLoaded } = useMaps();

  const submit = async (data: FormValues) => {
    setOpen(false);
    const filters: ActivityFilters = {};
    filters.sort = data.sort;
    if (data.endDate && data.enableEndDate) {
      filters.startDateBefore = data.endDate.toJSON();
    }
    if (data.startDate && data.enableStartDate) {
      filters.startDateAfter = data.startDate.toJSON();
    }
    if (data.title) {
      filters.search = data.title;
    }
    if (data.level && data.enableTrainingLevel) {
      filters['trainingLevel.level'] = data.level;
    }
    if (data.radius && data.enableGeoLocation) {
      filters.range = data.radius;
    }
    if (location && data.enableGeoLocation) {
      filters.lat = location.lat;
      filters.lng = location.lng;
    }
    updateFilters(filters);
  };

  const resetFilters = async () => {
    setOpen(false);
    reset({
      title: '',
      level: '',
      endDate: undefined,
      startDate: undefined,
      enableStartDate: false,
      enableEndDate: false,
      enableTrainingLevel: false,
      enableGeoLocation: false,
      sort: SORT_OPTIONS[0].key,
    });
    updateFilters({});
  };

  const onPlaceChanged = () => {
    if (autocomplete && autocomplete.getPlace().geometry) {
      const latLng = autocomplete.getPlace().geometry.location.toJSON() as LatLng;
      setLocation(latLng);
    }
  };

  const onKeyPressed = useCallback((e: React.KeyboardEvent<HTMLDivElement>) => {
    e.key === 'Enter' && e.preventDefault();
  }, []);

  const Input = forwardRef(({ ...props }: InputBaseProps, ref) => (
    <InputBase {...props} className={classes.input} inputRef={ref} name={props.name} placeholder='Søk etter aktiviteter' />
  ));
  Input.displayName = 'Input';

  return (
    <Paper className={classes.paper} noPadding>
      <form onSubmit={handleSubmit(submit)}>
        <div className={classes.root}>
          <Hidden xlUp>
            <IconButton aria-label='Se filtermeny' className={classes.iconButton} onClick={() => setOpen((prev) => !prev)}>
              <FilterIcon />
            </IconButton>
          </Hidden>
          <Input {...register('title')} />
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
            <Typography variant='caption'>Tid</Typography>
            <div className={classnames(classes.grid, classes.row)}>
              <Bool className={classes.margin} control={control} formState={formState} name='enableStartDate' type='checkbox' />
              <DatePicker control={control} formState={formState} fullWidth label='Fra' margin='dense' name='startDate' type='date-time' />
            </div>
            <div className={classnames(classes.grid, classes.row)}>
              <Bool className={classes.margin} control={control} formState={formState} name='enableEndDate' type='checkbox' />
              <DatePicker control={control} formState={formState} fullWidth label='Til' margin='dense' name='endDate' type='date-time' />
            </div>
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
            <Typography variant='caption'>Område i kart</Typography>
            <div className={classes.mapFilter}>
              {isMapLoaded && (
                <>
                  <div className={classnames(classes.grid, classes.row)}>
                    <Bool className={classes.margin} control={control} formState={formState} name='enableGeoLocation' type='checkbox' />
                    <Autocomplete onLoad={(data) => setAutocomplete(data)} onPlaceChanged={onPlaceChanged}>
                      <TextField className={classes.mapSearch} fullWidth onKeyPress={onKeyPressed} placeholder='Søk etter sted...' />
                    </Autocomplete>
                  </div>
                  <Slider control={control} name='radius' />
                  <GoogleMap center={location} mapContainerClassName={classes.mapContainerStyle} zoom={11}>
                    {radius && <Circle center={location} radius={radius * 1000} />}
                    <Data
                      options={{
                        drawingMode: 'Point',
                        // eslint-disable-next-line @typescript-eslint/no-explicit-any
                        featureFactory: (geo: any) => !geo?.g || setLocation(geo.g?.toJSON()),
                      }}
                    />
                  </GoogleMap>
                </>
              )}
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

export default ActivitiesSearch;

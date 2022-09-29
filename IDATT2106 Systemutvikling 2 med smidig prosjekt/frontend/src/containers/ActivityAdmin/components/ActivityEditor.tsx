import { useCallback, useRef, useState, useEffect } from 'react';
import classnames from 'classnames';
import { useForm, SubmitHandler } from 'react-hook-form';
import { TrainingLevel } from 'types/Enums';
import { Activity, LatLng } from 'types/Types';
import { useActivityById, useCreateActivity, useUpdateActivity, useDeleteActivity } from 'hooks/Activities';
import { useSnackbar } from 'hooks/Snackbar';
import { parseISO } from 'date-fns';
import { GoogleMap as GoogleMapRef, Marker, Autocomplete, Data } from '@react-google-maps/api';
import { useMaps } from 'hooks/Utils';
import { traningLevelToText } from 'utils';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import { Grid, LinearProgress, MenuItem, Button, TextField as MuiTextField, Typography, Collapse } from '@material-ui/core';

// Project components
import DatePicker from 'components/inputs/DatePicker';
import { ImageUpload } from 'components/inputs/Upload';
import VerifyDialog from 'components/layout/VerifyDialog';
import Dialog from 'components/layout/Dialog';
import Paper from 'components/layout/Paper';
import SubmitButton from 'components/inputs/SubmitButton';
import TextField from 'components/inputs/TextField';
import Select from 'components/inputs/Select';
import Bool from 'components/inputs/Bool';
import EquipmentEditor from 'containers/ActivityAdmin/components/EquipmentEditor';
import GoogleMap from 'components/miscellaneous/GoogleMap';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gridGap: theme.spacing(2),
    gridTemplateColumns: '1fr 1fr',
    [theme.breakpoints.down('sm')]: {
      gridGap: 0,
      gridTemplateColumns: '1fr',
    },
  },
  margin: {
    margin: theme.spacing(2, 0, 1),
    borderRadius: theme.shape.borderRadius,
    overflow: 'hidden',
  },
  red: {
    color: theme.palette.error.main,
    borderColor: theme.palette.error.main,
    '&:hover': {
      borderColor: theme.palette.error.light,
    },
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

export type ActivityEditorProps = {
  activityId: string | null;
  goToActivity: (newActivity: string | null) => void;
};

type FormValues = Pick<Activity, 'title' | 'description' | 'capacity' | 'level' | 'images' | 'inviteOnly' | 'equipment'> & {
  startDate: Date;
  endDate: Date;
  signupStart: Date;
  signupEnd: Date;
  geoLocation: LatLng | null;
};

const ActivityEditor = ({ activityId, goToActivity }: ActivityEditorProps) => {
  const classes = useStyles();
  const [openImages, setOpenImages] = useState(false);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [autocomplete, setAutocomplete] = useState<any>(null);
  const [center, setCenter] = useState<LatLng>({ lat: 60, lng: 10 });
  const mapRef = useRef<GoogleMapRef>(null);
  const { data, isLoading } = useActivityById(activityId || '');
  const createActivity = useCreateActivity();
  const updateActivity = useUpdateActivity(activityId || '');
  const deleteActivity = useDeleteActivity(activityId || '');
  const showSnackbar = useSnackbar();
  const { control, handleSubmit, register, trigger, formState, setError, reset, setValue, watch } = useForm<FormValues>();
  const { isLoaded: isMapLoaded } = useMaps();
  const geoLocation = watch('geoLocation');

  const setValues = useCallback(
    (newValues: Activity | null) => {
      reset({
        capacity: newValues?.capacity || 0,
        description: newValues?.description || '',
        endDate: newValues?.endDate ? parseISO(newValues.endDate) : new Date(),
        equipment: newValues?.equipment || [],
        level: newValues?.level || TrainingLevel.MEDIUM,
        geoLocation: newValues?.geoLocation || null,
        images: newValues?.images || [],
        inviteOnly: newValues?.inviteOnly || false,
        startDate: newValues?.startDate ? parseISO(newValues.startDate) : new Date(),
        signupEnd: newValues?.signupEnd ? parseISO(newValues.signupEnd) : new Date(),
        signupStart: newValues?.signupStart ? parseISO(newValues.signupStart) : new Date(),
        title: newValues?.title || '',
      });
    },
    [reset],
  );

  useEffect(() => {
    if (data) {
      setValues(data);
    } else {
      setValues(null);
    }
  }, [data, setValues]);

  const remove = async () => {
    deleteActivity.mutate(null, {
      onSuccess: (data) => {
        showSnackbar(data.message, 'success');
        goToActivity(null);
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const cancelActivity = async () => {
    await updateActivity.mutate({ ...data, closed: true } as Activity, {
      onSuccess: () => {
        showSnackbar('Aktiviteten ble avlyst', 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const submit: SubmitHandler<FormValues> = async (data) => {
    if (data.signupEnd < data.signupStart) {
      setError('signupEnd', { message: 'Påmeldingsslutt må være etter påmeldingsstart' });
      return;
    }
    if (data.startDate < data.signupEnd) {
      setError('signupEnd', { message: 'Påmeldingsslutt må være før start på aktivitet' });
      return;
    }
    if (data.endDate < data.startDate) {
      setError('endDate', { message: 'Slutt på aktivitet må være etter start på aktivitet' });
      return;
    }
    const activity = {
      ...data,
      geoLocation: geoLocation,
      startDate: data.startDate.toJSON(),
      endDate: data.endDate.toJSON(),
      signupEnd: data.signupEnd.toJSON(),
      signupStart: data.signupStart.toJSON(),
    };
    if (activityId) {
      await updateActivity.mutate(activity, {
        onSuccess: () => {
          showSnackbar('Aktiviteten ble oppdatert', 'success');
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    } else {
      await createActivity.mutate(activity, {
        onSuccess: (newActivity) => {
          showSnackbar('Aktiviteten ble opprettet', 'success');
          goToActivity(newActivity.id);
        },
        onError: (e) => {
          showSnackbar(e.message, 'error');
        },
      });
    }
  };

  const onPlaceChanged = () => {
    if (autocomplete && autocomplete.getPlace().geometry) {
      const latLng = autocomplete.getPlace().geometry.location.toJSON() as LatLng;
      mapRef?.current?.state?.map?.panTo(latLng);
      setCenter(latLng);
      setValue('geoLocation', latLng);
    }
  };

  const onKeyPressed = useCallback((e: React.KeyboardEvent<HTMLDivElement>) => {
    e.key === 'Enter' && e.preventDefault();
  }, []);

  if (isLoading) {
    return <LinearProgress />;
  }

  return (
    <>
      <form onSubmit={handleSubmit(submit)}>
        <Grid container direction='column' wrap='nowrap'>
          <div className={classes.grid}>
            <TextField formState={formState} label='Tittel' {...register('title', { required: 'Feltet er påkrevd' })} required />
            <TextField
              formState={formState}
              inputProps={{ inputMode: 'numeric' }}
              label='Antall plasser'
              {...register('capacity', {
                pattern: { value: RegExp(/^[0-9]*$/), message: 'Skriv inn et heltall som 0 eller høyere' },
                valueAsNumber: true,
                min: { value: 0, message: 'Antall plasser må være 0 eller høyere' },
                required: 'Feltet er påkrevd',
              })}
              required
            />
          </div>
          <div className={classes.grid}>
            <DatePicker control={control} formState={formState} label='Start' name='startDate' rules={{ required: 'Feltet er påkrevd' }} type='date-time' />
            <DatePicker control={control} formState={formState} label='Slutt' name='endDate' rules={{ required: 'Feltet er påkrevd' }} type='date-time' />
          </div>
          <div className={classes.grid}>
            <DatePicker
              control={control}
              formState={formState}
              label='Start påmelding'
              name='signupStart'
              rules={{ required: 'Feltet er påkrevd' }}
              type='date-time'
            />
            <DatePicker
              control={control}
              formState={formState}
              label='Slutt påmelding'
              name='signupEnd'
              rules={{ required: 'Feltet er påkrevd' }}
              type='date-time'
            />
          </div>
          <div className={classes.grid}>
            <Select control={control} formState={formState} label='Trenings-nivå' name='level'>
              {Object.values(TrainingLevel).map((value, index) => (
                <MenuItem key={index} value={value}>
                  {traningLevelToText(value as TrainingLevel)}
                </MenuItem>
              ))}
            </Select>
            <Bool className={classes.margin} control={control} formState={formState} label='Åpen kun for inviterte' name='inviteOnly' type='checkbox' />
          </div>
          <div className={classes.grid}>
            <Button onClick={() => setOpenImages(true)} variant='outlined'>
              Endre bilder
            </Button>
            <Dialog onClose={() => setOpenImages(false)} open={openImages} titleText='Endre bilder'>
              <ImageUpload formState={formState} label='Legg til bilde' name='images' register={register('images')} setValue={setValue} watch={watch} />
            </Dialog>
            <EquipmentEditor control={control} formState={formState} name='equipment' register={register} trigger={trigger} />
          </div>
          <TextField
            formState={formState}
            label='Beskrivelse'
            maxRows={15}
            minRows={5}
            multiline
            {...register('description', { required: 'Gi arrangementet en beskrivelse' })}
            required
          />
          <Paper className={classes.mapFilter}>
            <Typography variant='subtitle2'>Plassering - søk eller klikk på kartet</Typography>
            {isMapLoaded && (
              <>
                <Autocomplete onLoad={(data) => setAutocomplete(data)} onPlaceChanged={onPlaceChanged}>
                  <MuiTextField className={classes.mapSearch} fullWidth onKeyPress={onKeyPressed} placeholder='Søk etter sted...' />
                </Autocomplete>
                <GoogleMap center={center} mapContainerClassName={classes.mapContainerStyle} ref={mapRef} zoom={5}>
                  {geoLocation && <Marker position={geoLocation} />}
                  <Data
                    options={{
                      drawingMode: 'Point',
                      // eslint-disable-next-line @typescript-eslint/no-explicit-any
                      featureFactory: (geo: any) => !geo?.g || setValue('geoLocation', geo.g?.toJSON()),
                    }}
                  />
                </GoogleMap>
                <Collapse in={Boolean(geoLocation)}>
                  <Button className={classes.mapFilter} fullWidth onClick={() => setValue('geoLocation', null)} variant='outlined'>
                    Fjern plassering
                  </Button>
                </Collapse>
              </>
            )}
          </Paper>
          <SubmitButton className={classes.margin} disabled={isLoading} formState={formState}>
            {activityId ? 'Oppdater aktivitet' : 'Opprett aktivitet'}
          </SubmitButton>
          {Boolean(activityId) && (
            <div className={classes.grid}>
              {!data?.closed && (
                <VerifyDialog
                  className={classnames(classes.margin, classes.red)}
                  contentText='Hvis du avlyser aktiviteten så er det ikke mulig å aktivere den igjen'
                  onConfirm={cancelActivity}>
                  Avlys aktivitet
                </VerifyDialog>
              )}
              <VerifyDialog
                className={classnames(classes.margin, classes.red)}
                contentText='Hvis du sletter aktiviteten så er det ikke mulig å gjenopprette den igjen'
                onConfirm={remove}>
                Slett aktivitet
              </VerifyDialog>
            </div>
          )}
        </Grid>
      </form>
    </>
  );
};

export default ActivityEditor;

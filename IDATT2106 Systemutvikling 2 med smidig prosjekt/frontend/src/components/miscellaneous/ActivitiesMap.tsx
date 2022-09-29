import { useMemo, useRef } from 'react';
import { ActivityList, PaginationResponse } from 'types/Types';
import { InfiniteQueryObserverResult } from 'react-query';
import URLS from 'URLS';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'hooks/Snackbar';
import { useMaps } from 'hooks/Utils';
import { GoogleMap as GoogleMapRef, Marker } from '@react-google-maps/api';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import { Button } from '@material-ui/core';

// Project components
import GoogleMap from 'components/miscellaneous/GoogleMap';

const useStyles = makeStyles((theme) => ({
  containerStyle: {
    height: 500,
    marginBottom: theme.spacing(1),
  },
}));

export type ActivitiesMapProps = {
  useHook: (userId?: string, args?: unknown) => InfiniteQueryObserverResult<PaginationResponse<ActivityList>>;
  hookArgs?: Record<string, string | number>;
  userId?: string;
};

const ActivitiesMap = ({ useHook, hookArgs, userId }: ActivitiesMapProps) => {
  const classes = useStyles();
  const navigate = useNavigate();
  const showSnackbar = useSnackbar();
  const mapRef = useRef<GoogleMapRef>(null);
  const { isLoaded: isMapLoaded } = useMaps();
  const { data } = useHook(userId, { ...hookArgs });
  const activities = useMemo(
    () =>
      data
        ? data.pages
            .map((page) => page.content)
            .flat()
            .filter((activity) => activity.geoLocation !== null)
        : [],
    [data],
  );

  const findUserLocation = () => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;
        mapRef?.current?.state?.map?.panTo({ lat, lng });
      },
      (e) => showSnackbar(e.message, 'error'),
    );
  };

  if (!isMapLoaded) {
    return null;
  }
  return (
    <div>
      <GoogleMap mapContainerClassName={classes.containerStyle} ref={mapRef} zoom={8}>
        {activities.map((activity) => (
          <Marker key={activity.id} onClick={() => navigate(`${URLS.ACTIVITIES}${activity.id}/`)} position={activity.geoLocation} title={activity.title} />
        ))}
      </GoogleMap>
      {Boolean(navigator.geolocation) && (
        <Button fullWidth onClick={findUserLocation} variant='outlined'>
          Gå til min plassering
        </Button>
      )}
    </div>
  );
};

export default ActivitiesMap;

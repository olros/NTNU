import { forwardRef, ReactNode } from 'react';
import classnames from 'classnames';
import { useMaps } from 'hooks/Utils';
import { GoogleMap as Map, GoogleMapProps as MapProps } from '@react-google-maps/api';

// Material-UI
import { makeStyles, useTheme } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  mapStyle: {
    width: '100%',
    borderRadius: theme.shape.borderRadius,
  },
}));

const darkMapStyle = [
  { elementType: 'geometry', stylers: [{ color: '#242f3e' }] },
  { elementType: 'labels.text.stroke', stylers: [{ color: '#242f3e' }] },
  { elementType: 'labels.text.fill', stylers: [{ color: '#746855' }] },
  {
    featureType: 'administrative.locality',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#d59563' }],
  },
  {
    featureType: 'poi',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#d59563' }],
  },
  {
    featureType: 'poi.park',
    elementType: 'geometry',
    stylers: [{ color: '#263c3f' }],
  },
  {
    featureType: 'poi.park',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#6b9a76' }],
  },
  {
    featureType: 'road',
    elementType: 'geometry',
    stylers: [{ color: '#38414e' }],
  },
  {
    featureType: 'road',
    elementType: 'geometry.stroke',
    stylers: [{ color: '#212a37' }],
  },
  {
    featureType: 'road',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#9ca5b3' }],
  },
  {
    featureType: 'road.highway',
    elementType: 'geometry',
    stylers: [{ color: '#746855' }],
  },
  {
    featureType: 'road.highway',
    elementType: 'geometry.stroke',
    stylers: [{ color: '#1f2835' }],
  },
  {
    featureType: 'road.highway',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#f3d19c' }],
  },
  {
    featureType: 'transit',
    elementType: 'geometry',
    stylers: [{ color: '#2f3948' }],
  },
  {
    featureType: 'transit.station',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#d59563' }],
  },
  {
    featureType: 'water',
    elementType: 'geometry',
    stylers: [{ color: '#17263c' }],
  },
  {
    featureType: 'water',
    elementType: 'labels.text.fill',
    stylers: [{ color: '#515c6d' }],
  },
  {
    featureType: 'water',
    elementType: 'labels.text.stroke',
    stylers: [{ color: '#17263c' }],
  },
];

export type GoogleMapProps = MapProps & {
  children?: ReactNode;
};

const GoogleMap = forwardRef<Map, GoogleMapProps>(({ children, mapContainerClassName, ...props }: GoogleMapProps, ref) => {
  const classes = useStyles();
  const theme = useTheme();
  const { isLoaded: isMapLoaded } = useMaps();

  if (!isMapLoaded) {
    return null;
  }
  return (
    <Map
      center={{ lat: 60, lng: 10 }}
      mapContainerClassName={classnames(classes.mapStyle, mapContainerClassName)}
      options={{ streetViewControl: false, styles: theme.palette.mode === 'dark' ? darkMapStyle : undefined }}
      ref={ref}
      zoom={8}
      {...props}>
      {children}
    </Map>
  );
});
GoogleMap.displayName = 'GoogleMap';

export default GoogleMap;

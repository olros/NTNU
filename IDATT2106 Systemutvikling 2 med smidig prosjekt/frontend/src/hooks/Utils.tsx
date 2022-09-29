import { EffectCallback, useEffect, useRef } from 'react';
import { useLoadScript, LoadScriptProps } from '@react-google-maps/api';
import { GOOGLE_MAPS_API_KEY } from 'constant';

export const useInterval = (callback: EffectCallback, msDelay: number | null) => {
  const savedCallback = useRef<EffectCallback>();

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    const tick = () => {
      if (savedCallback.current) {
        savedCallback.current();
      }
    };

    if (msDelay) {
      const id = setInterval(tick, msDelay);
      return () => clearInterval(id);
    }
  }, [msDelay]);
};

const MAPS_LIBRARIES: LoadScriptProps['libraries'] = ['places'];

export const useMaps = () =>
  useLoadScript({
    googleMapsApiKey: GOOGLE_MAPS_API_KEY,
    libraries: MAPS_LIBRARIES,
  });

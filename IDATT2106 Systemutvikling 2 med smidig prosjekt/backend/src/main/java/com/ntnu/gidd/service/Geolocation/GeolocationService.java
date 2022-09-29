package com.ntnu.gidd.service.Geolocation;

import com.ntnu.gidd.dto.geolocation.GeoLocationDto;
import org.springframework.stereotype.Service;

public interface GeolocationService {
	
	GeoLocationDto findOrCreate(Double lat, Double lng);
}

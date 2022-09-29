package com.ntnu.gidd.service.Geolocation;

import com.ntnu.gidd.dto.geolocation.GeoLocationDto;
import com.ntnu.gidd.model.GeoLocation;
import com.ntnu.gidd.model.GeoLocationId;
import com.ntnu.gidd.repository.GeoLocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of a geolocation service
 */
@Service
public class GeolocationServiceImpl implements GeolocationService {
	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	GeoLocationRepository geoLocationRepository;

	/**
	 * Method to find a geolocation if it exists or create it if it doesn't
	 * @param lat latitude of the geolocation
	 * @param lng longitude of the geolocation
	 * @return the found or created geolocation
	 */
	@Override
	public GeoLocationDto findOrCreate(Double lat, Double lng) {
		Optional<GeoLocation> loc = geoLocationRepository.findById(new GeoLocationId(lat, lng));
		if (loc.isEmpty()) {
			return modelMapper.map(geoLocationRepository.saveAndFlush(new GeoLocation(lat, lng)), GeoLocationDto.class);
		}
		return modelMapper.map(loc.get(), GeoLocationDto.class);
	}
}

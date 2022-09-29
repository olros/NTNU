package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.GeoLocation;
import com.ntnu.gidd.model.GeoLocationId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GeoLocationRepository extends JpaRepository<GeoLocation, GeoLocationId> {

}

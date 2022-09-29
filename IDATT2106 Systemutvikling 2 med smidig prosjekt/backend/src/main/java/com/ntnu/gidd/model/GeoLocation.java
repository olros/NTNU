package com.ntnu.gidd.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {

    @EmbeddedId
    private GeoLocationId id;

    public GeoLocation(Double lat, Double lng) {
        this.id = new GeoLocationId(lat, lng);
    }

    @Transient
    public Double getLat(){
        return id.getLat();
    }
    @Transient
    public Double getLng() {
        return id.getLng();
    }
}
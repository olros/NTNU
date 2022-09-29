package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.GeoLocation;
import org.springframework.beans.factory.FactoryBean;

import java.util.Random;

public class GeoLocationFactory implements FactoryBean<GeoLocation> {

    Random random = new Random();

    @Override
    public GeoLocation getObject() throws Exception {
        return new GeoLocation(random.nextDouble(), random.nextDouble());
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}

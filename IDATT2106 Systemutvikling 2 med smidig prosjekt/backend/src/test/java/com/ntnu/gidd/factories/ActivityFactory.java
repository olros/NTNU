package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Equipment;
import org.springframework.beans.factory.FactoryBean;

import java.time.ZonedDateTime;
import java.util.*;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;

public class ActivityFactory implements FactoryBean<Activity> {

    Random random = new Random();
    TrainingLevelFactory trainingLevelFactory = new TrainingLevelFactory();
    GeoLocationFactory geoLocationFactory = new GeoLocationFactory();

    @Override
    public Activity getObject() throws Exception {
        return Activity.builder()
                .id(UUID.randomUUID())
                .title(getRandomString(5))
                .description(getRandomString(10))
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .signupStart(ZonedDateTime.now())
                .signupEnd(ZonedDateTime.now())
                .closed(false)
                .capacity(random.nextInt(1000)+100)
                .trainingLevel(trainingLevelFactory.getObject())
                .geoLocation(geoLocationFactory.getObject())
                .inviteOnly(false)
                .invites(List.of())
                .hosts(List.of())
                .likes(List.of())
                .build();
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

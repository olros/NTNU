package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.util.TrainingLevelEnum;
import org.springframework.beans.factory.FactoryBean;

import java.util.Random;


public class TrainingLevelFactory implements FactoryBean<TrainingLevel> {

        Random random = new Random();

        @Override
        public TrainingLevel getObject() throws Exception {
            return TrainingLevel.builder()
                    .id(1l)
                    .level(TrainingLevelEnum.High)
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

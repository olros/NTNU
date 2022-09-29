package com.ntnu.gidd.config;

import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.repository.TrainingLevelRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.util.TrainingLevelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    TrainingLevelRepository trainingLevelRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;

        if (trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.High).isEmpty()){
            trainingLevelRepository.save(TrainingLevel.builder().id(1L).level(TrainingLevelEnum.High).build());
        }
        if (trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.Medium).isEmpty()){
            trainingLevelRepository.save(TrainingLevel.builder().id(2L).level(TrainingLevelEnum.Medium).build());
        }
        if (trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.Low).isEmpty()){
            trainingLevelRepository.save(TrainingLevel.builder().id(3L).level(TrainingLevelEnum.Low).build());
        }

        alreadySetup = true;
    }
}
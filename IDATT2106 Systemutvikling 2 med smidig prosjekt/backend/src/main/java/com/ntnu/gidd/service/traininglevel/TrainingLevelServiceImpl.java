package com.ntnu.gidd.service.traininglevel;

import com.ntnu.gidd.exception.TrainingLevelNotFound;
import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.repository.TrainingLevelRepository;
import com.ntnu.gidd.util.TrainingLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of a Training level service
 */
@Service
public class TrainingLevelServiceImpl implements TrainingLevelService {

    @Autowired
    private TrainingLevelRepository trainingLevelRepository;

    /**
     * Method to get entity for low training level
     * @return  low training level entity
     */
    @Override
    public TrainingLevel getTrainingLevelLow() {
        return trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.Low)
                .orElseThrow(TrainingLevelNotFound::new);
    }

    /**
     * Method to get entity for Medium training level
     * @return  Medium training level entity
     */
    @Override
    public TrainingLevel getTrainingLevelMedium() {
        return trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.Medium)
                .orElseThrow(TrainingLevelNotFound::new);
    }

    /**
     * Method to get entity for High training level
     * @return  High training level entity
     */
    @Override
    public TrainingLevel getTrainingLevelHigh() {
        return trainingLevelRepository.findTrainingLevelByLevel(TrainingLevelEnum.High)
                .orElseThrow(TrainingLevelNotFound::new);    }
}

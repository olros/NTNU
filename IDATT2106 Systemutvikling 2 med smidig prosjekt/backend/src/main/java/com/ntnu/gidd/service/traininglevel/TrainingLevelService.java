package com.ntnu.gidd.service.traininglevel;

import com.ntnu.gidd.model.TrainingLevel;

public interface TrainingLevelService {
    TrainingLevel getTrainingLevelLow();
    TrainingLevel getTrainingLevelMedium();
    TrainingLevel getTrainingLevelHigh();
}

package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.TrainingLevel;
import com.ntnu.gidd.util.TrainingLevelEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingLevelRepository extends JpaRepository<TrainingLevel, Long> {
    Optional<TrainingLevel> findTrainingLevelByLevel(TrainingLevelEnum level);
}

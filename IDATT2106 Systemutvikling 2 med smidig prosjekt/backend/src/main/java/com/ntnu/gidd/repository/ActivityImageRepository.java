package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface  ActivityImageRepository extends JpaRepository<ActivityImage, Long> {

    void deleteActivityImageByActivityId(UUID activityId);
}

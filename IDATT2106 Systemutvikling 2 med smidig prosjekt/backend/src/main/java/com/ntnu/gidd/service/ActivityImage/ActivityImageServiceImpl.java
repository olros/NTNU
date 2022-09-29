package com.ntnu.gidd.service.ActivityImage;

import com.ntnu.gidd.dto.ActivityImageDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.ActivityImage;
import com.ntnu.gidd.repository.ActivityImageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of a activity Service
 */
@Service
public class ActivityImageServiceImpl implements ActivityImageService {
    @Autowired
    private ActivityImageRepository activityImageRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method to save all images on a activity
     * @param images the images to save
     * @param activity the activity that the images belongs to
     * @return the list of the saved images
     */
    public List<ActivityImage> saveActivityImage(List<ActivityImage> images, Activity activity){
        images.forEach(s -> s.setActivityId(activity.getId()));
       return activityImageRepository.saveAll(images);
    }

    /**
     * Method to update images on a activity
     * @param images the images to save
     * @param activity the activity that the images belongs to
     * @return the list of the updated images
     */

    public  List<ActivityImage> updateActivityImage(List<ActivityImageDto> images, Activity activity) {
        activityImageRepository.deleteActivityImageByActivityId(activity.getId());
        activityImageRepository.flush();
        List<ActivityImage> imageList = images.stream().map(s -> modelMapper.map(s, ActivityImage.class)).collect(Collectors.toList());
        imageList.forEach(s -> {
            s.setActivityId(activity.getId());
            s.setId(UUID.randomUUID());
        });
        return  activityImageRepository.saveAll(imageList);
    }
}

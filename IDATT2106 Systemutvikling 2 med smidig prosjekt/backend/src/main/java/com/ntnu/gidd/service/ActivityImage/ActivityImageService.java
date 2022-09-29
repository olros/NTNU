package com.ntnu.gidd.service.ActivityImage;

import com.ntnu.gidd.dto.ActivityImageDto;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.ActivityImage;

import java.util.List;

public interface ActivityImageService {
    List<ActivityImage> saveActivityImage(List<ActivityImage> images, Activity activity);
    public  List<ActivityImage> updateActivityImage(List<ActivityImageDto> images, Activity activity);
}

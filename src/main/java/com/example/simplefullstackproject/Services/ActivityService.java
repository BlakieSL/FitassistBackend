package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Repositories.ActivityRepository;
import com.example.simplefullstackproject.Services.Mappers.ActivityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ValidationHelper validationHelper;
    private final ActivityDtoMapper activityDtoMapper;
    public ActivityService(ActivityRepository activityRepository,
                           ValidationHelper validationHelper,
                           ActivityDtoMapper activityDtoMapper){
        this.activityRepository = activityRepository;
        this.validationHelper = validationHelper;
        this.activityDtoMapper = activityDtoMapper;
    }
    @Transactional

}

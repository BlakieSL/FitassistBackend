package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.CalculateCaloriesBurntRequest;
import com.example.simplefullstackproject.Models.Activity;
import com.example.simplefullstackproject.Repositories.ActivityRepository;
import com.example.simplefullstackproject.Services.Mappers.ActivityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    public ActivityDto saveActivity(ActivityDto activityDto){
        validationHelper.validate(activityDto);

        Activity activity = activityRepository.save(activityDtoMapper.map(activityDto));
        return activityDtoMapper.map(activity);
    }
    public ActivityDto getActivityById(Integer id){
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + id + " not found"));
        return activityDtoMapper.map(activity);
    }
    public List<ActivityDto> getActivities(){
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public ActivityDto calculateCaloriesBurnt(int id, CalculateCaloriesBurntRequest request){
        validationHelper.validate(request);

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + id + " not found"));

        activity.setCaloriesPerMinute(activity.getCaloriesPerMinute() * request.getTime());

        return activityDtoMapper.map(activity);
    }

}

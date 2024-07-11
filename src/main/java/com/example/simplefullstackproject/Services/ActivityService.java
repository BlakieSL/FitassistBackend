package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.ActivityCategoryDto;
import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Dtos.CalculateCaloriesBurntRequest;
import com.example.simplefullstackproject.Models.Activity;
import com.example.simplefullstackproject.Models.ActivityCategory;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Repositories.ActivityCategoryRepository;
import com.example.simplefullstackproject.Repositories.ActivityRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
import com.example.simplefullstackproject.Services.Mappers.ActivityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;
    private final ActivityDtoMapper activityDtoMapper;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    public ActivityService(ValidationHelper validationHelper,
                           CalculationsHelper calculationsHelper,
                           ActivityDtoMapper activityDtoMapper,
                           ActivityRepository activityRepository,
                           UserRepository userRepository,
                           ActivityCategoryRepository activityCategoryRepository){
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
        this.activityDtoMapper = activityDtoMapper;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.activityCategoryRepository = activityCategoryRepository;
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

    public List<ActivityCategoryDto> getCategories(){
        List<ActivityCategory> categories = activityCategoryRepository.findAll();
        return categories.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<ActivityDto> getActivitiesByCategory(Integer categoryId){
        List<Activity> activities = activityRepository.findAllByActivityCategory_Id(categoryId);
        return activities.stream()
                .map(activityDtoMapper::map)
                .collect(Collectors.toList());

    }

    public ActivityDtoResponse calculateCaloriesBurnt(int id,CalculateCaloriesBurntRequest request){
        validationHelper.validate(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User with id: " + request.getUserId() + " not found"));

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity with id: " + id + " not found"));

        ActivityDtoResponse response = activityDtoMapper.mapCalculated(activity);
        response.setTime(request.getTime());
        response.setCaloriesBurn((int) calculationsHelper.calculateCaloriesBurned(response.getTime(), user.getWeight(), activity.getMet()));
        return response;
    }

}

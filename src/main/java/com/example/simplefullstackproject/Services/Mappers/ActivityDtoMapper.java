package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Dtos.ActivityDtoResponse;
import com.example.simplefullstackproject.Models.Activity;
import org.springframework.stereotype.Service;

@Service
public class ActivityDtoMapper {
    public ActivityDto map(Activity activity){
        return new ActivityDto(
                activity.getId(),
                activity.getName(),
                activity.getMet());
    }

    public ActivityDtoResponse mapCalculated(Activity activity){
        ActivityDtoResponse response = new ActivityDtoResponse();
        response.setId(activity.getId());
        response.setName(activity.getName());
        return response;
    }
    public Activity map(ActivityDto request){
        Activity activity = new Activity();
        activity.setName(request.getName());
        activity.setMet(request.getMet());
        return activity;
    }

}

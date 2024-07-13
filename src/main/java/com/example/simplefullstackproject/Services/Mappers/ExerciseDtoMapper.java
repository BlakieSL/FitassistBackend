package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.ExerciseDto;
import com.example.simplefullstackproject.Models.Exercise;
import org.springframework.stereotype.Service;

@Service
public class ExerciseDtoMapper {
    public ExerciseDto map(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getText()
        );
    }

    public Exercise map(ExerciseDto request) {
        Exercise exercise = new Exercise();
        exercise.setName(request.getName());
        exercise.setText(request.getText());
        return exercise;
    }
}

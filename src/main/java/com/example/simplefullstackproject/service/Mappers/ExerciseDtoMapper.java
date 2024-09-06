package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.model.Exercise;
import org.springframework.stereotype.Service;

@Service
public class ExerciseDtoMapper {
    /*
    public ExerciseDto map(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getText()
        );
    }

    public Exercise map(ExerciseDto request) {
        Exercise exercise = new Exercise();
        exercise.setName(request.getName());
        exercise.setDescription(request.getDescription());
        exercise.setText(request.getText());
        return exercise;
    }

     */
}

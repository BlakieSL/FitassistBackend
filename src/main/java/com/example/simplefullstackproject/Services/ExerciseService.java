package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.ExerciseDto;
import com.example.simplefullstackproject.Models.Exercise;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Models.UserExercise;
import com.example.simplefullstackproject.Repositories.ExerciseRepository;
import com.example.simplefullstackproject.Repositories.UserExerciseRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
import com.example.simplefullstackproject.Services.Mappers.ExerciseDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ValidationHelper validationHelper;
    private final ExerciseDtoMapper exerciseDtoMapper;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final UserExerciseRepository userExerciseRepository;
    public ExerciseService(ValidationHelper validationHelper,
                           ExerciseDtoMapper exerciseDtoMapper,
                           ExerciseRepository exerciseRepository,
                           UserRepository userRepository,
                           UserExerciseRepository userExerciseRepository) {
        this.validationHelper = validationHelper;
        this.exerciseDtoMapper = exerciseDtoMapper;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.userExerciseRepository = userExerciseRepository;
    }

    @Transactional
    public ExerciseDto saveExercise(ExerciseDto exerciseDto) {
        validationHelper.validate(exerciseDto);

        Exercise exercise = exerciseRepository.save(exerciseDtoMapper.map(exerciseDto));
        return exerciseDtoMapper.map(exercise);
    }

    public ExerciseDto getExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " + id + " not found"));
        return exerciseDtoMapper.map(exercise);
    }

    public List<ExerciseDto> getExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByUserID(Integer userId) {
        List<UserExercise> userExercises = userExerciseRepository.findByUserId(userId);
        List<Exercise> exercises = userExercises.stream()
                .map(UserExercise::getExercise)
                .collect(Collectors.toList());
        return exercises.stream()
                .map(exerciseDtoMapper::map)
                .collect(Collectors.toList());
    }
}


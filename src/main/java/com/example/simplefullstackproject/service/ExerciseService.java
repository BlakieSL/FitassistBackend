package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.ExerciseCategoryDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.dto.SearchDtoRequest;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.ExerciseMapper;
import com.example.simplefullstackproject.model.Exercise;
import com.example.simplefullstackproject.model.ExerciseCategory;
import com.example.simplefullstackproject.model.ExerciseCategoryAssociation;
import com.example.simplefullstackproject.model.UserExercise;
import com.example.simplefullstackproject.repository.ExerciseCategoryAssociationRepository;
import com.example.simplefullstackproject.repository.ExerciseCategoryRepository;
import com.example.simplefullstackproject.repository.ExerciseRepository;
import com.example.simplefullstackproject.repository.UserExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ValidationHelper validationHelper;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;
    public ExerciseService(ValidationHelper validationHelper,
                           ExerciseMapper exerciseMapper,
                           ExerciseRepository exerciseRepository,
                           UserExerciseRepository userExerciseRepository,
                           ExerciseCategoryRepository exerciseCategoryRepository,
                           ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository) {
        this.validationHelper = validationHelper;
        this.exerciseMapper = exerciseMapper;
        this.exerciseRepository = exerciseRepository;
        this.userExerciseRepository = userExerciseRepository;
        this.exerciseCategoryRepository = exerciseCategoryRepository;
        this.exerciseCategoryAssociationRepository = exerciseCategoryAssociationRepository;
    }

    @Transactional
    public ExerciseDto saveExercise(ExerciseDto dto) {
        validationHelper.validate(dto);
        Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
        return exerciseMapper.toDto(exercise);
    }

    public ExerciseDto getExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " + id + " not found"));
        return exerciseMapper.toDto(exercise);
    }

    public List<ExerciseDto> getExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByUserID(Integer userId) {
        List<UserExercise> userExercises = userExerciseRepository
                .findByUserId(userId);
        List<Exercise> exercises = userExercises
                .stream()
                .map(UserExercise::getExercise)
                .collect(Collectors.toList());
        return exercises
                .stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> searchExercises(SearchDtoRequest dto){
        List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(dto.getName());
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseCategoryDto> getCategories() {
        List<ExerciseCategory> categories = exerciseCategoryRepository.findAll();
        return categories.stream()
                .map(exerciseMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByCategory(Integer categoryId) {
        List<ExerciseCategoryAssociation> exerciseCategoryAssociations = exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId);
        List<Exercise> exercises = exerciseCategoryAssociations.stream()
                .map(ExerciseCategoryAssociation::getExercise)
                .collect(Collectors.toList());
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }
}


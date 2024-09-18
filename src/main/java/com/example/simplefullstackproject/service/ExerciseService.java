package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.ExerciseAdditionDto;
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
    public ExerciseDto saveExercise(ExerciseAdditionDto dto) {
        validationHelper.validate(dto);
        Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
        return exerciseMapper.toDto(exercise);
    }

    public ExerciseDto getExerciseById(int id) {
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

    public List<ExerciseDto> getExercisesByUserID(int userId) {
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

    public List<ExerciseDto> getExercisesByCategory(int categoryId) {
        List<ExerciseCategoryAssociation> exerciseCategoryAssociations = exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId);
        List<Exercise> exercises = exerciseCategoryAssociations.stream()
                .map(ExerciseCategoryAssociation::getExercise)
                .collect(Collectors.toList());
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByExpertiseLevel(int expertiseLevelId) {
        List<Exercise> exercises = exerciseRepository.findByExpertiseLevel_Id(expertiseLevelId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ExerciseDto> getExercisesByForceType(int forceTypeId) {
        List<Exercise> exercises = exerciseRepository.findByForceType_Id(forceTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByMechanicsType(int mechanicsTypeId) {
        List<Exercise> exercises = exerciseRepository.findByMechanicsType_Id(mechanicsTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByEquipment(int exerciseEquipmentId) {
        List<Exercise> exercises = exerciseRepository.findByExerciseEquipment_Id(exerciseEquipmentId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDto> getExercisesByType(int exerciseTypeId) {
        List<Exercise> exercises = exerciseRepository.findByExerciseType_Id(exerciseTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }
}


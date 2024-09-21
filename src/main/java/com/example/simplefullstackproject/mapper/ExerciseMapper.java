package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.model.*;
import com.example.simplefullstackproject.repository.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper {

    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;

    @Autowired
    private ExpertiseLevelRepository expertiseLevelRepository;

    @Autowired
    private ForceTypeRepository forceTypeRepository;

    @Autowired
    private MechanicsTypeRepository mechanicsTypeRepository;

    @Autowired
    private ExerciseEquipmentRepository exerciseEquipmentRepository;

    @Autowired
    private ExerciseTypeRepository exerciseTypeRepository;

    @Mapping(target = "categories", source = "exerciseCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    @Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseToShortDto")
    @Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsToShortDto")
    @Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceToShortDto")
    @Mapping(target = "exerciseEquipment", source = "exerciseEquipment", qualifiedByName = "mapEquipmentToShortDto")
    @Mapping(target = "exerciseType", source = "exerciseType", qualifiedByName = "mapTypeToShortDto")
    public abstract ExerciseDto toDto(Exercise exercise);

    @Mapping(target = "exerciseCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
    @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
    @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
    @Mapping(target = "exerciseEquipment", source = "exerciseEquipmentId", qualifiedByName = "mapExerciseEquipment")
    @Mapping(target = "exerciseType", source = "exerciseTypeId", qualifiedByName = "mapExerciseType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user_exercise", ignore = true)
    @Mapping(target = "workoutSet", ignore = true)
    public abstract Exercise toEntity(ExerciseAdditionDto dto);

    public abstract ExerciseCategoryDto toCategoryDto(ExerciseCategory exerciseCategory);

    public abstract ExerciseInstructionDto toInstructionDto(ExerciseInstruction exerciseInstruction);

    public abstract ExerciseTipDto toTipDto(ExerciseTip exerciseTip);


    @Named("mapCategoryIdsToAssociations")
    protected Set<ExerciseCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
        if (categoryIds == null) {
            return new HashSet<>();
        }

        Set<ExerciseCategoryAssociation> associations = new HashSet<>();

        for (Integer categoryId : categoryIds) {
            ExerciseCategory category = exerciseCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Category not found for id: " + categoryId));

            ExerciseCategoryAssociation association = new ExerciseCategoryAssociation();
            association.setExerciseCategory(category);
            associations.add(association);
        }

        return associations;
    }

    @Named("mapAssociationsToCategoryShortDto")
    protected List<ExerciseCategoryShortDto> mapAssociationsToCategoryShortDto(Set<ExerciseCategoryAssociation> associations) {
        return associations.stream()
                .map(association -> new ExerciseCategoryShortDto(
                        association.getExerciseCategory().getId(),
                        association.getExerciseCategory().getName(),
                        association.getPriority()))
                .toList();
    }

    @Named("mapExpertiseToShortDto")
    protected ExerciseCategoryShortDto mapExpertiseToShortDto(ExpertiseLevel expertiseLevel) {
        return new ExerciseCategoryShortDto(expertiseLevel.getId(), expertiseLevel.getName());
    }

    @Named("mapMechanicsToShortDto")
    protected ExerciseCategoryShortDto mapMechanicsToShortDto(MechanicsType mechanicsType) {
        return new ExerciseCategoryShortDto(mechanicsType.getId(), mechanicsType.getName());
    }

    @Named("mapForceToShortDto")
    protected ExerciseCategoryShortDto mapForceToShortDto(ForceType forceType) {
        return new ExerciseCategoryShortDto(forceType.getId(), forceType.getName());
    }

    @Named("mapEquipmentToShortDto")
    protected ExerciseCategoryShortDto mapEquipmentToShortDto(ExerciseEquipment exerciseEquipment) {
        return new ExerciseCategoryShortDto(exerciseEquipment.getId(), exerciseEquipment.getName());
    }

    @Named("mapTypeToShortDto")
    protected ExerciseCategoryShortDto mapTypeToShortDto(ExerciseType exerciseType) {
        return new ExerciseCategoryShortDto(exerciseType.getId(), exerciseType.getName());
    }

    @Named("mapExpertiseLevel")
    protected ExpertiseLevel mapExpertiseLevel(Integer expertiseLevelId) {
        return expertiseLevelRepository.findById(expertiseLevelId)
                .orElseThrow(() -> new NoSuchElementException("Expertise Level not found for id: " + expertiseLevelId));
    }

    @Named("mapMechanicsType")
    protected MechanicsType mapMechanicsType(Integer mechanicsTypeId) {
        return mechanicsTypeRepository.findById(mechanicsTypeId)
                .orElseThrow(() -> new NoSuchElementException("Mechanics Type not found for id: " + mechanicsTypeId));
    }

    @Named("mapForceType")
    protected ForceType mapForceType(Integer forceTypeId) {
        return forceTypeRepository.findById(forceTypeId)
                .orElseThrow(() -> new NoSuchElementException("Force Type not found for id: " + forceTypeId));
    }

    @Named("mapExerciseEquipment")
    protected ExerciseEquipment mapExerciseEquipment(Integer exerciseEquipmentId) {
        return exerciseEquipmentRepository.findById(exerciseEquipmentId)
                .orElseThrow(() -> new NoSuchElementException("Exercise Equipment not found for id: " + exerciseEquipmentId));
    }

    @Named("mapExerciseType")
    protected ExerciseType mapExerciseType(Integer exerciseTypeId) {
        return exerciseTypeRepository.findById(exerciseTypeId)
                .orElseThrow(() -> new NoSuchElementException("Exercise Type not found for id: " + exerciseTypeId));
    }
}

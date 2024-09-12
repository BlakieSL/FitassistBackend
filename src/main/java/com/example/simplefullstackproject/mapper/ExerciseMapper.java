package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.ExerciseAdditionDto;
import com.example.simplefullstackproject.dto.ExerciseCategoryDto;
import com.example.simplefullstackproject.dto.ExerciseCategoryShortDto;
import com.example.simplefullstackproject.dto.ExerciseDto;
import com.example.simplefullstackproject.model.Exercise;
import com.example.simplefullstackproject.model.ExerciseCategory;
import com.example.simplefullstackproject.model.ExerciseCategoryAssociation;
import com.example.simplefullstackproject.repository.ExerciseCategoryRepository;
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

    @Mapping(target = "categories", source = "exerciseCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    public abstract ExerciseDto toDto(Exercise exercise);

    @Mapping(target = "exerciseCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "user_exercise", ignore = true)
    @Mapping(target = "workoutSet", ignore = true)
    public abstract Exercise toEntity(ExerciseAdditionDto dto);

    public abstract ExerciseCategoryDto toCategoryDto(ExerciseCategory exerciseCategory);

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
                        association.getExerciseCategory().getName()
                ))
                .toList();
    }
}
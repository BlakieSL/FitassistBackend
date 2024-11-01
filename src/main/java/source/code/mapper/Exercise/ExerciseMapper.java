package source.code.mapper.Exercise;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.Other.ExerciseCategoryShortDto;
import source.code.dto.Request.Exercise.ExerciseCreateDto;
import source.code.dto.Request.Exercise.ExerciseUpdateDto;
import source.code.dto.Response.ExerciseResponseDto;
import source.code.model.Exercise.*;
import source.code.model.Other.Equipment;
import source.code.model.Other.ExpertiseLevel;
import source.code.model.Text.ExerciseInstruction;
import source.code.model.Text.ExerciseTip;
import source.code.repository.*;
import source.code.service.Declaration.Helpers.RepositoryHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper {
  @Autowired
  private RepositoryHelper repositoryHelper;

  @Autowired
  private TargetMuscleRepository targetMuscleRepository;

  @Autowired
  private ExpertiseLevelRepository expertiseLevelRepository;

  @Autowired
  private ForceTypeRepository forceTypeRepository;

  @Autowired
  private MechanicsTypeRepository mechanicsTypeRepository;

  @Autowired
  private EquipmentRepository equipmentRepository;

  @Mapping(target = "targetMuscles", source = "exerciseTargetMuscles", qualifiedByName = "mapAssociationsToCategoryShortDto")
  @Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseToShortDto")
  @Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsToShortDto")
  @Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceToShortDto")
  @Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToShortDto")
  public abstract ExerciseResponseDto toResponseDto(Exercise exercise);

  @Mapping(target = "exerciseTargetMuscles", source = "targetMusclesIds", qualifiedByName = "mapTargetMuscleIdsToAssociations")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
  @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
  @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
  @Mapping(target = "equipment", source = "equipmentId", qualifiedByName = "mapExerciseEquipment")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userExercises", ignore = true)
  @Mapping(target = "workoutSets", ignore = true)
  @Mapping(target = "exerciseInstructions", ignore = true)
  @Mapping(target = "exerciseTips", ignore = true)
  public abstract Exercise toEntity(ExerciseCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "exerciseTargetMuscles", source = "targetMuscleIds", qualifiedByName = "mapTargetMuscleIdsToAssociations")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
  @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
  @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
  @Mapping(target = "equipment", source = "equipmentId", qualifiedByName = "mapExerciseEquipment")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userExercises", ignore = true)
  @Mapping(target = "workoutSets", ignore = true)
  @Mapping(target = "exerciseInstructions", ignore = true)
  @Mapping(target = "exerciseTips", ignore = true)
  public abstract void updateExerciseFromDto(@MappingTarget Exercise exercise, ExerciseUpdateDto request);


  @AfterMapping
  protected void setExerciseAssociations(@MappingTarget Exercise exercise, ExerciseCreateDto dto) {
    Set<ExerciseInstruction> instructions = dto.getInstructions().stream()
            .map(instructionDto -> {
              ExerciseInstruction instruction = ExerciseInstruction
                      .createWithNumberAndText(instructionDto.getNumber(), instructionDto.getText());
              instruction.setExercise(exercise);
              return instruction;
            }).collect(Collectors.toSet());

    exercise.getExerciseInstructions().addAll(instructions);


    Set<ExerciseTip> tips = dto.getTips().stream()
            .map(tipDto -> {
              ExerciseTip tip = ExerciseTip
                      .createWithNumberAndText(tipDto.getNumber(), tipDto.getText());
              tip.setExercise(exercise);
              return tip;
            }).collect(Collectors.toSet());

    exercise.getExerciseTips().addAll(tips);
  }

  @Named("mapTargetMuscleIdsToAssociations")
  protected Set<ExerciseTargetMuscle> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<ExerciseTargetMuscle> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      TargetMuscle category = repositoryHelper
              .find(targetMuscleRepository, TargetMuscle.class, categoryId);

      ExerciseTargetMuscle association = ExerciseTargetMuscle
              .createWithTargetMuscle(category);

      associations.add(association);
    }

    return associations;
  }

  @Named("mapAssociationsToCategoryShortDto")
  protected List<ExerciseCategoryShortDto> mapAssociationsToCategoryShortDto(
          Set<ExerciseTargetMuscle> associations) {
    return associations.stream()
            .map(association -> new ExerciseCategoryShortDto(
                    association.getTargetMuscle().getId(),
                    association.getTargetMuscle().getName(),
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
  protected ExerciseCategoryShortDto mapEquipmentToShortDto(Equipment equipment) {
    return new ExerciseCategoryShortDto(equipment.getId(), equipment.getName());
  }

  @Named("mapExpertiseLevel")
  protected ExpertiseLevel mapExpertiseLevel(Integer expertiseLevelId) {
    return repositoryHelper.find(expertiseLevelRepository, ExpertiseLevel.class, expertiseLevelId);
  }

  @Named("mapMechanicsType")
  protected MechanicsType mapMechanicsType(Integer mechanicsTypeId) {
    return repositoryHelper.find(mechanicsTypeRepository, MechanicsType.class, mechanicsTypeId);
  }

  @Named("mapForceType")
  protected ForceType mapForceType(Integer forceTypeId) {
    return repositoryHelper.find(forceTypeRepository, ForceType.class, forceTypeId);
  }

  @Named("mapExerciseEquipment")
  protected Equipment mapExerciseEquipment(Integer equipmentId) {
    return repositoryHelper.find(equipmentRepository, Equipment.class, equipmentId);
  }
}

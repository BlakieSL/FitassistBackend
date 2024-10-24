package source.code.mapper.Exercise;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.other.ExerciseCategoryShortDto;
import source.code.dto.request.Exercise.ExerciseCreateDto;
import source.code.dto.request.Exercise.ExerciseInstructionCreateDto;
import source.code.dto.request.Exercise.ExerciseTipCreateDto;
import source.code.dto.request.Exercise.ExerciseUpdateDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.model.Exercise.*;
import source.code.repository.*;
import source.code.service.declaration.Helpers.RepositoryHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper {
  @Autowired
  private RepositoryHelper repositoryHelper;

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
  public abstract ExerciseResponseDto toResponseDto(Exercise exercise);

  @Mapping(target = "exerciseCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "exerciseInstructions", source = "instructions", qualifiedByName = "mapInstructions")
  @Mapping(target = "exerciseTips", source = "tips", qualifiedByName = "mapTips")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
  @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
  @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
  @Mapping(target = "exerciseEquipment", source = "exerciseEquipmentId", qualifiedByName = "mapExerciseEquipment")
  @Mapping(target = "exerciseType", source = "exerciseTypeId", qualifiedByName = "mapExerciseType")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userExercises", ignore = true)
  @Mapping(target = "workoutSet", ignore = true)
  public abstract Exercise toEntity(ExerciseCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "exerciseCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
  @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
  @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
  @Mapping(target = "exerciseEquipment", source = "exerciseEquipmentId", qualifiedByName = "mapExerciseEquipment")
  @Mapping(target = "exerciseType", source = "exerciseTypeId", qualifiedByName = "mapExerciseType")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userExercises", ignore = true)
  @Mapping(target = "workoutSet", ignore = true)
  public abstract void updateExerciseFromDto(@MappingTarget Exercise exercise, ExerciseUpdateDto request);


  @Named("mapCategoryIdsToAssociations")
  protected Set<ExerciseCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<ExerciseCategoryAssociation> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      ExerciseCategory category = repositoryHelper
              .find(exerciseCategoryRepository, ExerciseCategory.class, categoryId);

      ExerciseCategoryAssociation association = ExerciseCategoryAssociation
              .createWithExerciseCategory(category);

      associations.add(association);
    }

    return associations;
  }

  @Named("mapInstructions")
  protected Set<ExerciseInstruction> mapInstructions(List<ExerciseInstructionCreateDto> dto) {
    if(dto == null) {
      return new HashSet<>();
    }

    return dto.stream()
            .map(instructionDto -> {
              ExerciseInstruction instruction = ExerciseInstruction
                      .createWithNumberAndText(instructionDto.getNumber(), instructionDto.getText());

              return instruction;
            })
            .collect(Collectors.toSet());
  }

  @Named("mapTips")
  protected Set<ExerciseTip> mapTips(List<ExerciseTipCreateDto> dto) {
    if(dto == null) {
      return new HashSet<>();
    }

    return dto.stream()
            .map(tipDto -> {
              ExerciseTip tip = ExerciseTip
                      .createWithNumberAndText(tipDto.getNumber(), tipDto.getText());

              return tip;
            })
            .collect(Collectors.toSet());
  }

  @Named("mapAssociationsToCategoryShortDto")
  protected List<ExerciseCategoryShortDto> mapAssociationsToCategoryShortDto(
          Set<ExerciseCategoryAssociation> associations) {
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
  protected ExerciseEquipment mapExerciseEquipment(Integer exerciseEquipmentId) {
    return repositoryHelper.find(exerciseEquipmentRepository, ExerciseEquipment.class, exerciseEquipmentId);
  }

  @Named("mapExerciseType")
  protected ExerciseType mapExerciseType(Integer exerciseTypeId) {
    return repositoryHelper.find(exerciseTypeRepository, ExerciseType.class, exerciseTypeId);
  }
}

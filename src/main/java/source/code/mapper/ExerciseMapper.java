package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.exercise.ExerciseUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.dto.response.exercise.TargetMuscleResponseDto;
import source.code.dto.response.text.ExerciseInstructionResponseDto;
import source.code.dto.response.text.ExerciseTipResponseDto;
import source.code.mapper.helper.CommonMappingHelper;
import source.code.model.exercise.*;
import source.code.model.text.ExerciseInstruction;
import source.code.model.text.ExerciseTip;
import source.code.repository.*;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommonMappingHelper.class})
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

    @Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseToCategoryResponseDto")
    @Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsToCategoryResponseDto")
    @Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceToCategoryResponseDto")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToCategoryResponseDto")
    @Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
    @Mapping(target = "firstImageUrl", ignore = true)
    @Mapping(target = "interactionCreatedAt", ignore = true)
    @Mapping(target = "savesCount", ignore = true)
    @Mapping(target = "saved", ignore = true)
    public abstract ExerciseSummaryDto toSummaryDto(Exercise exercise);

    @Mapping(target = "targetMuscles", source = "exerciseTargetMuscles", qualifiedByName = "mapAssociationsToTargetMuscleResponseDto")
    @Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseToCategoryResponseDto")
    @Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsToCategoryResponseDto")
    @Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceToCategoryResponseDto")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToCategoryResponseDto")
    @Mapping(target = "instructions", source = "exerciseInstructions", qualifiedByName = "mapInstructionsToDto")
    @Mapping(target = "tips", source = "exerciseTips", qualifiedByName = "mapTipsToDto")
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "plans", ignore = true)
    @Mapping(target = "savesCount", ignore = true)
    @Mapping(target = "saved", ignore = true)
    public abstract ExerciseResponseDto toResponseDto(Exercise exercise);

    @Mapping(target = "exerciseTargetMuscles", source = "targetMusclesIds", qualifiedByName = "mapTargetMuscleIdsToAssociations")
    @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
    @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
    @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
    @Mapping(target = "equipment", source = "equipmentId", qualifiedByName = "mapExerciseEquipment")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userExercises", ignore = true)
    @Mapping(target = "workoutSetExercises", ignore = true)
    @Mapping(target = "exerciseInstructions", ignore = true)
    @Mapping(target = "exerciseTips", ignore = true)
    @Mapping(target = "mediaList", ignore = true)
    public abstract Exercise toEntity(ExerciseCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "exerciseTargetMuscles", source = "targetMuscleIds", qualifiedByName = "mapTargetMuscleIdsToAssociations")
    @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevel")
    @Mapping(target = "mechanicsType", source = "mechanicsTypeId", qualifiedByName = "mapMechanicsType")
    @Mapping(target = "forceType", source = "forceTypeId", qualifiedByName = "mapForceType")
    @Mapping(target = "equipment", source = "equipmentId", qualifiedByName = "mapExerciseEquipment")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userExercises", ignore = true)
    @Mapping(target = "workoutSetExercises", ignore = true)
    @Mapping(target = "exerciseInstructions", ignore = true)
    @Mapping(target = "exerciseTips", ignore = true)
    @Mapping(target = "mediaList", ignore = true)
    public abstract void updateExerciseFromDto(@MappingTarget Exercise exercise, ExerciseUpdateDto request);


    @AfterMapping
    protected void setExerciseAssociations(@MappingTarget Exercise exercise, ExerciseCreateDto dto) {
        List<ExerciseInstruction> instructions = dto.getInstructions().stream()
                .map(instructionDto -> {
                    ExerciseInstruction instruction = ExerciseInstruction
                            .of(instructionDto.getOrderIndex(), instructionDto.getText());
                    instruction.setExercise(exercise);
                    return instruction;
                }).toList();

        exercise.getExerciseInstructions().addAll(instructions);


        List<ExerciseTip> tips = dto.getTips().stream()
                .map(tipDto -> {
                    ExerciseTip tip = ExerciseTip
                            .createWithNumberAndText(tipDto.getOrderIndex(), tipDto.getText());
                    tip.setExercise(exercise);
                    return tip;
                }).toList();

        exercise.getExerciseTips().addAll(tips);
    }

    @Named("mapTargetMuscleIdsToAssociations")
    protected Set<ExerciseTargetMuscle> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
        return Optional.ofNullable(categoryIds)
                .orElseGet(List::of)
                .stream()
                .map(categoryId -> {
                    TargetMuscle category = repositoryHelper
                            .find(targetMuscleRepository, TargetMuscle.class, categoryId);
                    return ExerciseTargetMuscle.createWithTargetMuscle(category);
                })
                .collect(Collectors.toSet());
    }

    @Named("mapAssociationsToTargetMuscleResponseDto")
    protected List<TargetMuscleResponseDto> mapAssociationsToTargetMuscleResponseDto(
            Set<ExerciseTargetMuscle> associations) {
        return associations.stream()
                .map(association -> TargetMuscleResponseDto.create(
                        association.getTargetMuscle().getId(),
                        association.getTargetMuscle().getName(),
                        association.getPriority()))
                .toList();
    }

    @Named("mapExpertiseToCategoryResponseDto")
    protected CategoryResponseDto mapExpertiseToCategoryResponseDto(ExpertiseLevel expertiseLevel) {
        return new CategoryResponseDto(expertiseLevel.getId(), expertiseLevel.getName());
    }

    @Named("mapMechanicsToCategoryResponseDto")
    protected CategoryResponseDto mapMechanicsToCategoryResponseDto(MechanicsType mechanicsType) {
        return new CategoryResponseDto(mechanicsType.getId(), mechanicsType.getName());
    }

    @Named("mapForceToCategoryResponseDto")
    protected CategoryResponseDto mapForceToCategoryResponseDto(ForceType forceType) {
        return new CategoryResponseDto(forceType.getId(), forceType.getName());
    }

    @Named("mapEquipmentToCategoryResponseDto")
    protected CategoryResponseDto mapEquipmentToCategoryResponseDto(Equipment equipment) {
        return new CategoryResponseDto(equipment.getId(), equipment.getName());
    }

    @Named("mapExpertiseLevel")
    protected ExpertiseLevel mapExpertiseLevel(Integer expertiseLevelId) {
        return expertiseLevelRepository.getReferenceById(expertiseLevelId);
    }

    @Named("mapMechanicsType")
    protected MechanicsType mapMechanicsType(Integer mechanicsTypeId) {
        return mechanicsTypeRepository.getReferenceById(mechanicsTypeId);
    }

    @Named("mapForceType")
    protected ForceType mapForceType(Integer forceTypeId) {
        return forceTypeRepository.getReferenceById(forceTypeId);
    }

    @Named("mapExerciseEquipment")
    protected Equipment mapExerciseEquipment(Integer equipmentId) {
        return equipmentRepository.getReferenceById(equipmentId);
    }

    @Named("mapInstructionsToDto")
    protected List<ExerciseInstructionResponseDto> mapInstructionsToDto(Set<ExerciseInstruction> instructions) {
        return instructions.stream()
                .map(instruction -> new ExerciseInstructionResponseDto(
                        instruction.getId(),
                        instruction.getOrderIndex(),
                        instruction.getText()
                ))
                .toList();
    }

    @Named("mapTipsToDto")
    protected List<ExerciseTipResponseDto> mapTipsToDto(Set<ExerciseTip> tips) {
        return tips.stream()
                .map(tip -> new ExerciseTipResponseDto(tip.getId(), tip.getOrderIndex(), tip.getText()))
                .toList();
    }
}

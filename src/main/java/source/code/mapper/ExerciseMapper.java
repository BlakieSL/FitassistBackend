package source.code.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.exercise.ExerciseUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.dto.response.exercise.TargetMuscleResponseDto;
import source.code.dto.response.text.TextResponseDto;
import source.code.mapper.helper.CommonMappingHelper;
import source.code.model.exercise.*;
import source.code.model.text.ExerciseInstruction;
import source.code.model.text.ExerciseTip;
import source.code.repository.*;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ExerciseMapper {

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

	@Mapping(target = "expertiseLevel", source = "expertiseLevel",
			qualifiedByName = "mapExpertiseToCategoryResponseDto")
	@Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsToCategoryResponseDto")
	@Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceToCategoryResponseDto")
	@Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToCategoryResponseDto")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ExerciseSummaryDto toSummaryDto(Exercise exercise);

	@Mapping(target = "targetMuscles", source = "exerciseTargetMuscles",
			qualifiedByName = "mapAssociationsToTargetMuscleResponseDto")
	@Mapping(target = "expertiseLevel", source = "expertiseLevel",
			qualifiedByName = "mapExpertiseToCategoryResponseDto")
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

	@Mapping(target = "exerciseTargetMuscles", ignore = true)
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
	@Mapping(target = "exerciseTargetMuscles", ignore = true)
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
		if (dto.getInstructions() != null) {
			List<ExerciseInstruction> instructions = dto.getInstructions().stream().map(instructionDto -> {
				ExerciseInstruction instruction = ExerciseInstruction.of(instructionDto.getOrderIndex(),
						instructionDto.getText());
				instruction.setExercise(exercise);
				return instruction;
			}).toList();

			exercise.getExerciseInstructions().addAll(instructions);
		}

		if (dto.getTips() != null) {
			List<ExerciseTip> tips = dto.getTips().stream().map(tipDto -> {
				ExerciseTip tip = ExerciseTip.createWithNumberAndText(tipDto.getOrderIndex(), tipDto.getText());
				tip.setExercise(exercise);
				return tip;
			}).toList();

			exercise.getExerciseTips().addAll(tips);
		}

		if (dto.getTargetMusclesIds() != null) {
			List<ExerciseTargetMuscle> targetMuscles = targetMuscleRepository.findAllByIdIn(dto.getTargetMusclesIds())
				.stream()
				.map(tm -> ExerciseTargetMuscle.createWithTargetMuscleExercise(tm, exercise))
				.toList();

			exercise.getExerciseTargetMuscles().addAll(targetMuscles);
		}
	}

	@AfterMapping
	protected void updateTargetMuscleAssociations(@MappingTarget Exercise exercise, ExerciseUpdateDto dto) {
		if (dto.getTargetMuscleIds() == null) {
			return;
		}

		exercise.getExerciseTargetMuscles().clear();

		List<ExerciseTargetMuscle> targetMuscles = targetMuscleRepository.findAllByIdIn(dto.getTargetMuscleIds())
			.stream()
			.map(tm -> ExerciseTargetMuscle.createWithTargetMuscleExercise(tm, exercise))
			.toList();

		exercise.getExerciseTargetMuscles().addAll(targetMuscles);
	}

	@Named("mapAssociationsToTargetMuscleResponseDto")
	protected List<TargetMuscleResponseDto> mapAssociationsToTargetMuscleResponseDto(
			Set<ExerciseTargetMuscle> associations) {
		return associations.stream()
			.map(association -> TargetMuscleResponseDto.create(association.getTargetMuscle().getId(),
					association.getTargetMuscle().getName(), association.getPriority()))
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
	protected List<TextResponseDto> mapInstructionsToDto(Set<ExerciseInstruction> instructions) {
		return instructions.stream()
			.map(instruction -> new TextResponseDto(instruction.getId(), instruction.getOrderIndex(),
					instruction.getText(), null))
			.toList();
	}

	@Named("mapTipsToDto")
	protected List<TextResponseDto> mapTipsToDto(Set<ExerciseTip> tips) {
		return tips.stream()
			.map(tip -> new TextResponseDto(tip.getId(), tip.getOrderIndex(), tip.getText(), null))
			.toList();
	}

}

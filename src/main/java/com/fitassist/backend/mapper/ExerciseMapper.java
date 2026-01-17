package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.exercise.ExerciseUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.exercise.TargetMuscleResponseDto;
import com.fitassist.backend.dto.response.text.TextResponseDto;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.exercise.*;
import com.fitassist.backend.model.text.ExerciseInstruction;
import com.fitassist.backend.model.text.ExerciseTip;
import com.fitassist.backend.repository.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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

	@Autowired
	private CommonMappingHelper commonMappingHelper;

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
			List<ExerciseInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instructionDto -> ExerciseInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
						instructionDto.getText(), exercise))
				.toList();

			exercise.getExerciseInstructions().addAll(instructions);
		}

		if (dto.getTips() != null) {
			List<ExerciseTip> tips = dto.getTips()
				.stream()
				.map(tipDto -> ExerciseTip.of(tipDto.getOrderIndex(), tipDto.getTitle(), tipDto.getText(), exercise))
				.toList();

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
	protected void updateAssociations(@MappingTarget Exercise exercise, ExerciseUpdateDto dto) {
		if (dto.getTargetMuscleIds() != null) {
			exercise.getExerciseTargetMuscles().clear();

			List<ExerciseTargetMuscle> targetMuscles = targetMuscleRepository.findAllByIdIn(dto.getTargetMuscleIds())
				.stream()
				.map(tm -> ExerciseTargetMuscle.createWithTargetMuscleExercise(tm, exercise))
				.toList();

			exercise.getExerciseTargetMuscles().addAll(targetMuscles);
		}

		if (dto.getInstructions() != null) {
			commonMappingHelper.updateTextAssociations(exercise.getExerciseInstructions(), dto.getInstructions(),
					instructionDto -> ExerciseInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
							instructionDto.getText(), exercise));
		}

		if (dto.getTips() != null) {
			commonMappingHelper.updateTextAssociations(exercise.getExerciseTips(), dto.getTips(),
					tipDto -> ExerciseTip.of(tipDto.getOrderIndex(), tipDto.getTitle(), tipDto.getText(), exercise));
		}
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
					instruction.getText(), instruction.getTitle()))
			.toList();
	}

	@Named("mapTipsToDto")
	protected List<TextResponseDto> mapTipsToDto(Set<ExerciseTip> tips) {
		return tips.stream()
			.map(tip -> new TextResponseDto(tip.getId(), tip.getOrderIndex(), tip.getText(), tip.getTitle()))
			.toList();
	}

}

package com.fitassist.backend.mapper.exercise;

import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.exercise.ExerciseUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.exercise.TargetMuscleResponseDto;
import com.fitassist.backend.dto.response.text.TextResponseDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.exercise.*;
import com.fitassist.backend.model.text.ExerciseInstruction;
import com.fitassist.backend.model.text.ExerciseTip;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ExerciseMapper {

	private CommonMappingHelper commonMappingHelper;

	@Autowired
	private void setCommonMappingHelper(CommonMappingHelper commonMappingHelper) {
		this.commonMappingHelper = commonMappingHelper;
	}

	@Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseLevelToDto")
	@Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsTypeToDto")
	@Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceTypeToDto")
	@Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToDto")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ExerciseSummaryDto toSummaryDto(Exercise exercise);

	@Mapping(target = "targetMuscles", source = "exerciseTargetMuscles", qualifiedByName = "mapTargetMusclesToDto")
	@Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseLevelToDto")
	@Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsTypeToDto")
	@Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceTypeToDto")
	@Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToDto")
	@Mapping(target = "instructions", source = "exerciseInstructions", qualifiedByName = "mapInstructionsToDto")
	@Mapping(target = "tips", source = "exerciseTips", qualifiedByName = "mapTipsToDto")
	@Mapping(target = "imageUrls", ignore = true)
	@Mapping(target = "plans", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ExerciseResponseDto toResponseDto(Exercise exercise);

	@Mapping(target = "exerciseTargetMuscles", ignore = true)
	@Mapping(target = "expertiseLevel", expression = "java(context.getExpertiseLevel())")
	@Mapping(target = "mechanicsType", expression = "java(context.getMechanicsType())")
	@Mapping(target = "forceType", expression = "java(context.getForceType())")
	@Mapping(target = "equipment", expression = "java(context.getEquipment())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userExercises", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	@Mapping(target = "exerciseInstructions", ignore = true)
	@Mapping(target = "exerciseTips", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Exercise toEntity(ExerciseCreateDto dto, @Context ExerciseMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "exerciseTargetMuscles", ignore = true)
	@Mapping(target = "expertiseLevel", ignore = true)
	@Mapping(target = "mechanicsType", ignore = true)
	@Mapping(target = "forceType", ignore = true)
	@Mapping(target = "equipment", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userExercises", ignore = true)
	@Mapping(target = "workoutSetExercises", ignore = true)
	@Mapping(target = "exerciseInstructions", ignore = true)
	@Mapping(target = "exerciseTips", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void updateExerciseFromDto(@MappingTarget Exercise exercise, ExerciseUpdateDto request,
			@Context ExerciseMappingContext context);

	@AfterMapping
	protected void setExerciseAssociations(@MappingTarget Exercise exercise, ExerciseCreateDto dto,
			@Context ExerciseMappingContext context) {
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

		if (context.getTargetMuscles() != null) {
			List<ExerciseTargetMuscle> targetMuscles = context.getTargetMuscles()
				.stream()
				.map(tm -> ExerciseTargetMuscle.createWithTargetMuscleExercise(tm, exercise))
				.toList();

			exercise.getExerciseTargetMuscles().addAll(targetMuscles);
		}
	}

	@AfterMapping
	protected void updateAssociations(@MappingTarget Exercise exercise, ExerciseUpdateDto dto,
			@Context ExerciseMappingContext context) {
		if (context.getExpertiseLevel() != null) {
			exercise.setExpertiseLevel(context.getExpertiseLevel());
		}

		if (dto.getMechanicsTypeId() != null) {
			exercise.setMechanicsType(context.getMechanicsType());
		}

		if (dto.getForceTypeId() != null) {
			exercise.setForceType(context.getForceType());
		}

		if (dto.getEquipmentId() != null) {
			exercise.setEquipment(context.getEquipment());
		}

		if (context.getTargetMuscles() != null) {
			exercise.getExerciseTargetMuscles().clear();

			List<ExerciseTargetMuscle> targetMuscles = context.getTargetMuscles()
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

	@Named("mapTargetMusclesToDto")
	protected List<TargetMuscleResponseDto> mapTargetMusclesToDto(Set<ExerciseTargetMuscle> associations) {
		return associations.stream()
			.map(association -> TargetMuscleResponseDto.create(association.getTargetMuscle().getId(),
					association.getTargetMuscle().getName(), association.getPriority()))
			.toList();
	}

	@Named("mapExpertiseLevelToDto")
	protected CategoryResponseDto mapExpertiseLevelToDto(ExpertiseLevel expertiseLevel) {
		return new CategoryResponseDto(expertiseLevel.getId(), expertiseLevel.getName());
	}

	@Named("mapMechanicsTypeToDto")
	protected CategoryResponseDto mapMechanicsTypeToDto(MechanicsType mechanicsType) {
		return mechanicsType != null ? new CategoryResponseDto(mechanicsType.getId(), mechanicsType.getName()) : null;
	}

	@Named("mapForceTypeToDto")
	protected CategoryResponseDto mapForceTypeToDto(ForceType forceType) {
		return forceType != null ? new CategoryResponseDto(forceType.getId(), forceType.getName()) : null;
	}

	@Named("mapEquipmentToDto")
	protected CategoryResponseDto mapEquipmentToDto(Equipment equipment) {
		return equipment != null ? new CategoryResponseDto(equipment.getId(), equipment.getName()) : null;
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

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
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class ExerciseMapper {

	private CommonMappingHelper commonMappingHelper;

	@Autowired
	private void setCommonMappingHelper(CommonMappingHelper commonMappingHelper) {
		this.commonMappingHelper = commonMappingHelper;
	}

	@Mapping(target = "targetMuscles", source = "exerciseTargetMuscles", qualifiedByName = "mapTargetMusclesToResponse")
	@Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseLevelToResponse")
	@Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsTypeToResponse")
	@Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceTypeToResponse")
	@Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToResponse")
	@Mapping(target = "instructions", source = "exerciseInstructions", qualifiedByName = "mapInstructionsToResponse")
	@Mapping(target = "tips", source = "exerciseTips", qualifiedByName = "mapTipsToResponse")
	@Mapping(target = "imageUrls", ignore = true)
	@Mapping(target = "plans", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ExerciseResponseDto toResponse(Exercise exercise);

	@Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseLevelToResponse")
	@Mapping(target = "mechanicsType", source = "mechanicsType", qualifiedByName = "mapMechanicsTypeToResponse")
	@Mapping(target = "forceType", source = "forceType", qualifiedByName = "mapForceTypeToResponse")
	@Mapping(target = "equipment", source = "equipment", qualifiedByName = "mapEquipmentToResponse")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract ExerciseSummaryDto toSummary(Exercise exercise);

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

	@AfterMapping
	protected void setAssociations(@MappingTarget Exercise exercise, ExerciseCreateDto dto,
			@Context ExerciseMappingContext context) {
		if (dto.getInstructions() != null) {
			List<ExerciseInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instDto -> ExerciseInstruction.of(instDto.getOrderIndex(), instDto.getTitle(), instDto.getText(),
						exercise))
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
				.map(targetMuscle -> ExerciseTargetMuscle.createWithTargetMuscleExercise(targetMuscle, exercise))
				.toList();

			exercise.getExerciseTargetMuscles().addAll(targetMuscles);
		}
	}

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
	public abstract void update(@MappingTarget Exercise exercise, ExerciseUpdateDto request,
			@Context ExerciseMappingContext context);

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
				.map(targetMuscle -> ExerciseTargetMuscle.createWithTargetMuscleExercise(targetMuscle, exercise))
				.toList();

			exercise.getExerciseTargetMuscles().addAll(targetMuscles);
		}

		if (dto.getInstructions() != null) {
			commonMappingHelper.updateTextAssociations(exercise.getExerciseInstructions(), dto.getInstructions(),
					instDto -> ExerciseInstruction.of(instDto.getOrderIndex(), instDto.getTitle(), instDto.getText(),
							exercise));
		}

		if (dto.getTips() != null) {
			commonMappingHelper.updateTextAssociations(exercise.getExerciseTips(), dto.getTips(),
					tipDto -> ExerciseTip.of(tipDto.getOrderIndex(), tipDto.getTitle(), tipDto.getText(), exercise));
		}
	}

	@Named("mapTargetMusclesToResponse")
	protected List<TargetMuscleResponseDto> mapTargetMusclesToResponse(Set<ExerciseTargetMuscle> associations) {
		return associations.stream()
			.map(association -> TargetMuscleResponseDto.create(association.getTargetMuscle().getId(),
					association.getTargetMuscle().getName(), association.getPriority()))
			.toList();
	}

	@Named("mapExpertiseLevelToResponse")
	protected CategoryResponseDto mapExpertiseLevelToResponse(ExpertiseLevel expertiseLevel) {
		return new CategoryResponseDto(expertiseLevel.getId(), expertiseLevel.getName());
	}

	@Named("mapMechanicsTypeToResponse")
	protected CategoryResponseDto mapMechanicsTypeToResponse(MechanicsType mechanicsType) {
		return Optional.ofNullable(mechanicsType)
			.map(mt -> new CategoryResponseDto(mt.getId(), mt.getName()))
			.orElse(null);
	}

	@Named("mapForceTypeToResponse")
	protected CategoryResponseDto mapForceTypeToResponse(ForceType forceType) {
		return Optional.ofNullable(forceType).map(ft -> new CategoryResponseDto(ft.getId(), ft.getName())).orElse(null);
	}

	@Named("mapEquipmentToResponse")
	protected CategoryResponseDto mapEquipmentToResponse(Equipment equipment) {
		return Optional.ofNullable(equipment).map(e -> new CategoryResponseDto(e.getId(), e.getName())).orElse(null);
	}

	@Named("mapInstructionsToResponse")
	protected List<TextResponseDto> mapInstructionsToResponse(Set<ExerciseInstruction> instructions) {
		return instructions.stream()
			.map(instruction -> new TextResponseDto(instruction.getId(), instruction.getOrderIndex(),
					instruction.getText(), instruction.getTitle()))
			.toList();
	}

	@Named("mapTipsToResponse")
	protected List<TextResponseDto> mapTipsToResponse(Set<ExerciseTip> tips) {
		return tips.stream()
			.map(tip -> new TextResponseDto(tip.getId(), tip.getOrderIndex(), tip.getText(), tip.getTitle()))
			.toList();
	}

}

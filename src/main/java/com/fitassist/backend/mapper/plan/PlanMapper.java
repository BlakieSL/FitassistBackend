package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.dto.response.plan.WorkoutResponseDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.plan.PlanCategoryAssociation;
import com.fitassist.backend.model.text.PlanInstruction;
import com.fitassist.backend.model.workout.Workout;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutMapper.class, CommonMappingHelper.class })
public abstract class PlanMapper {

	private WorkoutMapper workoutMapper;

	private CommonMappingHelper commonMappingHelper;

	@Autowired
	public void setWorkoutMapper(WorkoutMapper workoutMapper) {
		this.workoutMapper = workoutMapper;
	}

	@Autowired
	public void setCommonMappingHelper(CommonMappingHelper commonMappingHelper) {
		this.commonMappingHelper = commonMappingHelper;
	}

	@Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapCategoriesToResponses")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "instructions", source = "planInstructions")
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "totalWeeks", ignore = true)
	@Mapping(target = "imageUrls", ignore = true)
	public abstract PlanResponseDto toResponse(Plan plan);

	@AfterMapping
	protected void calculateTotalWeeks(@MappingTarget PlanResponseDto dto, Plan plan) {
		int currentDay = 0;
		for (WorkoutResponseDto workoutDto : dto.getWorkouts()) {
			workoutDto.setWeekIndex((currentDay / 7) + 1);
			workoutDto.setDayOfWeekIndex((currentDay % 7) + 1);

			currentDay += workoutDto.getRestDaysAfter() + 1;
		}

		dto.setTotalWeeks((int) Math.ceil(currentDay / 7.0));
	}

	@Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapCategoriesToResponses")
	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "firstImageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract PlanSummaryDto toSummary(Plan plan);

	@Mapping(target = "planCategoryAssociations", ignore = true)
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "planInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Plan toEntity(PlanCreateDto dto, @Context PlanMappingContext context);

	@AfterMapping
	protected void setAssociations(@MappingTarget Plan plan, PlanCreateDto dto, @Context PlanMappingContext context) {
		if (dto.getInstructions() != null) {
			List<PlanInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instructionDto -> PlanInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
						instructionDto.getText(), plan))
				.toList();

			plan.getPlanInstructions().addAll(instructions);
		}

		if (context.getCategories() != null) {
			List<PlanCategoryAssociation> categories = context.getCategories()
				.stream()
				.map(category -> PlanCategoryAssociation.createWithPlanAndCategory(plan, category))
				.toList();

			plan.getPlanCategoryAssociations().addAll(categories);
		}

		plan.getWorkouts().forEach(workout -> workout.setPlan(plan));
	}

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "planCategoryAssociations", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "workouts", ignore = true)
	@Mapping(target = "planInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget Plan plan, PlanUpdateDto planUpdateDto,
			@Context PlanMappingContext context);

	@AfterMapping
	protected void updateAssociations(@MappingTarget Plan plan, PlanUpdateDto dto,
			@Context PlanMappingContext context) {
		if (context.getCategories() != null) {
			plan.getPlanCategoryAssociations().clear();

			List<PlanCategoryAssociation> associations = context.getCategories()
				.stream()
				.map(category -> PlanCategoryAssociation.createWithPlanAndCategory(plan, category))
				.toList();

			plan.getPlanCategoryAssociations().addAll(associations);
		}

		if (dto.getInstructions() != null) {
			commonMappingHelper.updateTextAssociations(plan.getPlanInstructions(), dto.getInstructions(),
					instructionDto -> PlanInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
							instructionDto.getText(), plan));
		}

		if (dto.getWorkouts() != null) {
			Set<Workout> existingWorkouts = plan.getWorkouts();

			List<Integer> updatedWorkoutIds = dto.getWorkouts()
				.stream()
				.map(WorkoutNestedUpdateDto::getId)
				.filter(Objects::nonNull)
				.toList();

			existingWorkouts.removeIf(workout -> !updatedWorkoutIds.contains(workout.getId()));

			for (WorkoutNestedUpdateDto workoutDto : dto.getWorkouts()) {
				if (workoutDto.getId() != null) {
					existingWorkouts.stream()
						.filter(workout -> workout.getId().equals(workoutDto.getId()))
						.findFirst()
						.ifPresent(workout -> workoutMapper.update(workout, workoutDto, context));
				}
				else {
					Workout newWorkout = workoutMapper.toEntity(workoutDto, context);
					newWorkout.setPlan(plan);
					existingWorkouts.add(newWorkout);
				}
			}
		}
	}

	@Named("mapCategoriesToResponses")
	protected List<CategoryResponseDto> mapCategoriesToResponses(Set<PlanCategoryAssociation> associations) {
		return associations.stream()
			.map(association -> new CategoryResponseDto(association.getPlanCategory().getId(),
					association.getPlanCategory().getName()))
			.toList();
	}

}

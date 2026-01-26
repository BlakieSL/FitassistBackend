package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.model.plan.PlanCategoryAssociation;
import com.fitassist.backend.model.text.PlanInstruction;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.workout.Workout;
import com.fitassist.backend.repository.PlanCategoryRepository;
import com.fitassist.backend.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { WorkoutMapper.class, CommonMappingHelper.class })
public abstract class PlanMapper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlanCategoryRepository planCategoryRepository;

	@Autowired
	private WorkoutMapper workoutMapper;

	@Autowired
	private CommonMappingHelper commonMappingHelper;

	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "totalWeeks", ignore = true)
	@Mapping(target = "categories", source = "planCategoryAssociations",
			qualifiedByName = "mapAssociationsToCategoryResponseDto")
	@Mapping(target = "instructions", source = "planInstructions")
	@Mapping(target = "imageUrls", ignore = true)
	public abstract PlanResponseDto toResponseDto(Plan plan);

	@Mapping(target = "author", source = "user", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "firstImageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "categories", source = "planCategoryAssociations",
			qualifiedByName = "mapAssociationsToCategoryResponseDto")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "likesCount", ignore = true)
	@Mapping(target = "dislikesCount", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "liked", ignore = true)
	@Mapping(target = "disliked", ignore = true)
	@Mapping(target = "saved", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract PlanSummaryDto toSummaryDto(Plan plan);

	@Mapping(target = "planCategoryAssociations", ignore = true)
	@Mapping(target = "user", expression = "java(userIdToUser(userId))")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "planInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Plan toEntity(PlanCreateDto dto, @Context int userId);

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
	public abstract void updatePlan(@MappingTarget Plan plan, PlanUpdateDto planUpdateDto);

	@AfterMapping
	protected void setAssociations(@MappingTarget Plan plan, PlanCreateDto dto) {
		if (dto.getInstructions() != null) {
			List<PlanInstruction> instructions = dto.getInstructions()
				.stream()
				.map(instructionDto -> PlanInstruction.of(instructionDto.getOrderIndex(), instructionDto.getTitle(),
						instructionDto.getText(), plan))
				.toList();

			plan.getPlanInstructions().addAll(instructions);
		}

		if (dto.getCategoryIds() != null) {
			List<PlanCategoryAssociation> categories = planCategoryRepository.findAllByIdIn(dto.getCategoryIds())
				.stream()
				.map(category -> PlanCategoryAssociation.createWithPlanAndCategory(plan, category))
				.toList();

			plan.getPlanCategoryAssociations().addAll(categories);
		}

		plan.getWorkouts().forEach(workout -> workout.setPlan(plan));
	}

	@AfterMapping
	protected void updateAssociations(@MappingTarget Plan plan, PlanUpdateDto dto) {
		if (dto.getCategoryIds() != null) {
			plan.getPlanCategoryAssociations().clear();

			List<PlanCategory> categories = planCategoryRepository.findAllByIdIn(dto.getCategoryIds());

			List<PlanCategoryAssociation> associations = categories.stream()
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
						.ifPresent(workout -> workoutMapper.updateWorkoutNested(workout, workoutDto));
				}
				else {
					Workout newWorkout = workoutMapper.toEntityFromNested(workoutDto);
					newWorkout.setPlan(plan);
					existingWorkouts.add(newWorkout);
				}
			}
		}
	}

	@AfterMapping
	protected void calculateTotalWeeks(@MappingTarget PlanResponseDto dto, Plan plan) {
		int currentDay = 0;
		for (var workoutDto : dto.getWorkouts()) {
			workoutDto.setWeekIndex((currentDay / 7) + 1);
			workoutDto.setDayOfWeekIndex((currentDay % 7) + 1);

			currentDay += workoutDto.getRestDaysAfter() + 1;
		}

		dto.setTotalWeeks((int) Math.ceil(currentDay / 7.0));
	}

	@Named("userIdToUser")
	protected User userIdToUser(Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	@Named("mapAssociationsToCategoryResponseDto")
	protected List<CategoryResponseDto> mapAssociationsToCategoryResponseDto(
			Set<PlanCategoryAssociation> associations) {
		return associations.stream()
			.map(association -> new CategoryResponseDto(association.getPlanCategory().getId(),
					association.getPlanCategory().getName()))
			.toList();
	}

}

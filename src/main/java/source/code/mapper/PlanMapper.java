package source.code.mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.request.plan.PlanUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.helper.CommonMappingHelper;
import source.code.model.plan.Plan;
import source.code.model.plan.PlanCategory;
import source.code.model.plan.PlanCategoryAssociation;
import source.code.model.text.PlanInstruction;
import source.code.model.user.User;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring", uses = {WorkoutMapper.class, CommonMappingHelper.class})
public abstract class PlanMapper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Autowired
	private PlanCategoryRepository planCategoryRepository;

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
	@Mapping(target = "public", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	public abstract PlanSummaryDto toSummaryDto(Plan plan);

	@Mapping(target = "planCategoryAssociations", source = "categoryIds",
		qualifiedByName = "mapCategoryIdsToAssociations")
	@Mapping(target = "user", expression = "java(userIdToUser(userId))")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "workouts", ignore = true)
	@Mapping(target = "planInstructions", ignore = true)
	@Mapping(target = "views", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Plan toEntity(PlanCreateDto dto, @Context int userId);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "planCategoryAssociations", source = "categoryIds",
		qualifiedByName = "mapCategoryIdsToAssociations")
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
	protected void setPlanAssociations(@MappingTarget Plan plan, PlanCreateDto dto) {
		if (dto.getInstructions() == null) {
			return;
		}

		List<PlanInstruction> instructions = dto.getInstructions()
			.stream()
			.map(instructionDto -> PlanInstruction.of(instructionDto.getOrderIndex(), instructionDto.getText(),
				instructionDto.getText(), plan))
			.toList();

		plan.getPlanInstructions().addAll(instructions);
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

	@Named("mapCategoryIdsToAssociations")
	protected Set<PlanCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
		return Optional.ofNullable(categoryIds).orElseGet(List::of).stream().map(categoryId -> {
			PlanCategory category = repositoryHelper.find(planCategoryRepository, PlanCategory.class, categoryId);
			return PlanCategoryAssociation.createWithPlanCategory(category);
		}).collect(Collectors.toSet());
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

package source.code.mapper.plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.pojo.PlanCategoryShortDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.request.plan.PlanUpdateDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.model.other.ExpertiseLevel;
import source.code.model.plan.*;
import source.code.model.text.PlanInstruction;
import source.code.model.user.User;
import source.code.repository.*;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PlanMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RepositoryHelper repositoryHelper;
    @Autowired
    private PlanCategoryRepository planCategoryRepository;
    @Autowired
    private PlanTypeRepository planTypeRepository;
    @Autowired
    private ExpertiseLevelRepository expertiseLevelRepository;

    @Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    @Mapping(target = "planType", source = "planType", qualifiedByName = "mapTypeToShortDto")
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    public abstract PlanResponseDto toResponseDto(Plan plan);

    @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
    @Mapping(target = "user", expression = "java(userIdToUser(userId))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userPlans", ignore = true)
    @Mapping(target = "workouts", ignore = true)
    @Mapping(target = "planInstructions", ignore = true)
    public abstract Plan toEntity(PlanCreateDto dto, @Context int userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userPlans", ignore = true)
    @Mapping(target = "workouts", ignore = true)
    @Mapping(target = "planInstructions", ignore = true)
    public abstract void updatePlan(@MappingTarget Plan plan, PlanUpdateDto planUpdateDto);

    @AfterMapping
    protected void setPlanAssociations(@MappingTarget Plan plan, PlanCreateDto dto) {
        Set<PlanInstruction> instructions = dto.getInstructions().stream()
                .map(instructionDto -> PlanInstruction.of(
                        instructionDto.getNumber(),
                        instructionDto.getText(),
                        instructionDto.getText(),
                        plan
                )).collect(Collectors.toSet());

        plan.getPlanInstructions().addAll(instructions);
    }

    @Named("userToUserId")
    protected Integer userToUserId(User user) {
        return user.getId();
    }

    @Named("userIdToUser")
    protected User userIdToUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
    }

    @Named("mapCategoryIdsToAssociations")
    protected Set<PlanCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
        return Optional.ofNullable(categoryIds)
                .orElseGet(List::of)
                .stream()
                .map(categoryId -> {
                    PlanCategory category = repositoryHelper
                            .find(planCategoryRepository, PlanCategory.class, categoryId);
                    return PlanCategoryAssociation.createWithPlanCategory(category);
                })
                .collect(Collectors.toSet());
    }

    @Named("mapAssociationsToCategoryShortDto")
    protected List<PlanCategoryShortDto> mapAssociationsToCategoryShortDto(
            Set<PlanCategoryAssociation> associations) {
        return associations.stream()
                .map(association -> new PlanCategoryShortDto(
                        association.getPlanCategory().getId(),
                        association.getPlanCategory().getName()
                ))
                .toList();
    }

    @Named("mapTypeIdToEntity")
    protected PlanType mapTypeIdToEntity(int planTypeId) {
        return repositoryHelper.find(planTypeRepository, PlanType.class, planTypeId);
    }

    @Named("mapTypeToShortDto")
    protected PlanCategoryShortDto mapTypeToShortDto(PlanType planType) {
        return new PlanCategoryShortDto(planType.getId(), planType.getName());
    }
}

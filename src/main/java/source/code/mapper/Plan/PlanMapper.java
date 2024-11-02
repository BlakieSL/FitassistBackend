package source.code.mapper.Plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.POJO.PlanCategoryShortDto;
import source.code.dto.Request.Plan.PlanCreateDto;
import source.code.dto.Request.Plan.PlanUpdateDto;
import source.code.dto.Response.PlanResponseDto;
import source.code.model.Other.ExpertiseLevel;
import source.code.model.Plan.*;
import source.code.model.Text.PlanInstruction;
import source.code.repository.*;
import source.code.service.Declaration.Helpers.RepositoryHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PlanMapper {
  @Autowired
  private RepositoryHelper repositoryHelper;
  @Autowired
  private PlanCategoryRepository planCategoryRepository;
  @Autowired
  private PlanTypeRepository planTypeRepository;
  @Autowired
  private PlanDurationRepository planDurationRepository;
  @Autowired
  private EquipmentRepository equipmentRepository;
  @Autowired
  private ExpertiseLevelRepository expertiseLevelRepository;

  @Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
  @Mapping(target = "planType", source = "planType", qualifiedByName = "mapTypeToShortDto")
  @Mapping(target = "planDuration", source = "planDuration", qualifiedByName = "mapDurationToShortDto")
  @Mapping(target = "expertiseLevel", source = "expertiseLevel", qualifiedByName = "mapExpertiseLevelToShortDto")
  public abstract PlanResponseDto toResponseDto(Plan plan);

  @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
  @Mapping(target = "planDuration", source = "planDurationId", qualifiedByName = "mapDurationIdToEntity")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevelIdToEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userPlans", ignore = true)
  @Mapping(target = "workouts", ignore = true)
  @Mapping(target = "planInstructions", ignore = true)
  public abstract Plan toEntity(PlanCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
  @Mapping(target = "planDuration", source = "planDurationId", qualifiedByName = "mapDurationIdToEntity")
  @Mapping(target = "expertiseLevel", source = "expertiseLevelId", qualifiedByName = "mapExpertiseLevelIdToEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userPlans", ignore = true)
  @Mapping(target = "workouts", ignore = true)
  @Mapping(target = "planInstructions", ignore = true)
  public abstract void updatePlan(@MappingTarget Plan plan, PlanUpdateDto planUpdateDto);

  @AfterMapping
  protected void setPlanAssociations(@MappingTarget Plan plan, PlanCreateDto dto) {
    Set<PlanInstruction> instructions = dto.getInstructions().stream()
            .map(instructionDto -> {
              PlanInstruction instruction = PlanInstruction
                      .createWithNumberTitleText(instructionDto.getNumber(),
                              instructionDto.getText(), instructionDto.getText());

              instruction.setPlan(plan);
              return instruction;
            }).collect(Collectors.toSet());

    plan.getPlanInstructions().addAll(instructions);
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

  @Named("mapDurationIdToEntity")
  protected PlanDuration mapDurationIdToEntity(int planDurationId) {
    return repositoryHelper.find(planDurationRepository, PlanDuration.class, planDurationId);
  }

  @Named("mapExpertiseLevelIdToEntity")
  protected ExpertiseLevel mapExpertiseLevelIdToEntity(int expertiseLevelId) {
    return repositoryHelper.find(expertiseLevelRepository, ExpertiseLevel.class, expertiseLevelId);
  }

  @Named("mapTypeToShortDto")
  protected PlanCategoryShortDto mapTypeToShortDto(PlanType planType) {
    return new PlanCategoryShortDto(planType.getId(), planType.getName());
  }

  @Named("mapDurationToShortDto")
  protected PlanCategoryShortDto mapDurationToShortDto(PlanDuration planDuration) {
    return new PlanCategoryShortDto(planDuration.getId(), planDuration.getName());
  }

  @Named("mapExpertiseLevelToShortDto")
  protected PlanCategoryShortDto mapExpertiseLevelToShortDto(ExpertiseLevel expertiseLevel) {
    return new PlanCategoryShortDto(expertiseLevel.getId(), expertiseLevel.getName());
  }
}

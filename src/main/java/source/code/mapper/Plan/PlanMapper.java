package source.code.mapper.Plan;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.other.PlanCategoryShortDto;
import source.code.dto.request.Plan.PlanCreateDto;
import source.code.dto.request.Plan.PlanUpdateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.model.Plan.*;
import source.code.model.Text.PlanInstruction;
import source.code.model.Text.RecipeInstruction;
import source.code.repository.*;
import source.code.service.declaration.Helpers.RepositoryHelper;

import java.util.HashSet;
import java.util.List;
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
  private PlanEquipmentRepository planEquipmentRepository;
  @Autowired
  private PlanExpertiseLevelRepository planExpertiseLevelRepository;

  @Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
  @Mapping(target = "planType", source = "planType", qualifiedByName = "mapTypeToShortDto")
  @Mapping(target = "planDuration", source = "planDuration", qualifiedByName = "mapDurationToShortDto")
  @Mapping(target = "planEquipment", source = "planEquipment", qualifiedByName = "mapEquipmentToShortDto")
  @Mapping(target = "planExpertiseLevel", source = "planExpertiseLevel", qualifiedByName = "mapExpertiseLevelToShortDto")
  public abstract PlanResponseDto toResponseDto(Plan plan);

  @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
  @Mapping(target = "planDuration", source = "planDurationId", qualifiedByName = "mapDurationIdToEntity")
  @Mapping(target = "planEquipment", source = "planEquipmentId", qualifiedByName = "mapEquipmentIdToEntity")
  @Mapping(target = "planExpertiseLevel", source = "planExpertiseLevelId", qualifiedByName = "mapExpertiseLevelIdToEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userPlans", ignore = true)
  @Mapping(target = "workoutPlans", ignore = true)
  public abstract Plan toEntity(PlanCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
  @Mapping(target = "planDuration", source = "planDurationId", qualifiedByName = "mapDurationIdToEntity")
  @Mapping(target = "planEquipment", source = "planEquipmentId", qualifiedByName = "mapEquipmentIdToEntity")
  @Mapping(target = "planExpertiseLevel", source = "planExpertiseLevelId", qualifiedByName = "mapExpertiseLevelIdToEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userPlans", ignore = true)
  @Mapping(target = "workoutPlans", ignore = true)
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
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<PlanCategoryAssociation> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      PlanCategory category = repositoryHelper
              .find(planCategoryRepository, PlanCategory.class, categoryId);

      PlanCategoryAssociation association = PlanCategoryAssociation
              .createWithPlanCategory(category);

      associations.add(association);
    }

    return associations;
  }

  @Named("mapAssociationsToCategoryShortDto")
  protected List<PlanCategoryShortDto> mapAssociationsToCategoryShortDto(Set<PlanCategoryAssociation> associations) {
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

  @Named("mapEquipmentIdToEntity")
  protected PlanEquipment mapEquipmentIdToEntity(int planEquipmentId) {
    return repositoryHelper.find(planEquipmentRepository, PlanEquipment.class, planEquipmentId);
  }

  @Named("mapExpertiseLevelIdToEntity")
  protected PlanExpertiseLevel mapExpertiseLevelIdToEntity(int planExpertiseLevelId) {
    return repositoryHelper.find(planExpertiseLevelRepository, PlanExpertiseLevel.class, planExpertiseLevelId);
  }

  @Named("mapTypeToShortDto")
  protected PlanCategoryShortDto mapTypeToShortDto(PlanType planType) {
    return new PlanCategoryShortDto(planType.getId(), planType.getName());
  }

  @Named("mapDurationToShortDto")
  protected PlanCategoryShortDto mapDurationToShortDto(PlanDuration planDuration) {
    return new PlanCategoryShortDto(planDuration.getId(), planDuration.getName());
  }

  @Named("mapEquipmentToShortDto")
  protected PlanCategoryShortDto mapEquipmentToShortDto(PlanEquipment planEquipment) {
    return new PlanCategoryShortDto(planEquipment.getId(), planEquipment.getName());
  }

  @Named("mapExpertiseLevelToShortDto")
  protected PlanCategoryShortDto mapExpertiseLevelToShortDto(PlanExpertiseLevel planExpertiseLevel) {
    return new PlanCategoryShortDto(planExpertiseLevel.getId(), planExpertiseLevel.getName());
  }
}

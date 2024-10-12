package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.other.PlanCategoryShortDto;
import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.model.Plan.*;
import source.code.repository.*;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PlanMapper {
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
  public abstract PlanResponseDto toDto(Plan plan);

  @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
  @Mapping(target = "planType", source = "planTypeId", qualifiedByName = "mapTypeIdToEntity")
  @Mapping(target = "planDuration", source = "planDurationId", qualifiedByName = "mapDurationIdToEntity")
  @Mapping(target = "planEquipment", source = "planEquipmentId", qualifiedByName = "mapEquipmentIdToEntity")
  @Mapping(target = "planExpertiseLevel", source = "planExpertiseLevelId", qualifiedByName = "mapExpertiseLevelIdToEntity")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userPlans", ignore = true)
  @Mapping(target = "workoutPlans", ignore = true)
  public abstract Plan toEntity(PlanCreateDto dto);

  public abstract PlanCategoryResponseDto toCategoryDto(PlanCategory planCategory);

  @Named("mapCategoryIdsToAssociations")
  protected Set<PlanCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
    if (categoryIds == null) {
      return new HashSet<>();
    }

    Set<PlanCategoryAssociation> associations = new HashSet<>();

    for (Integer categoryId : categoryIds) {
      PlanCategory category = planCategoryRepository.findById(categoryId)
              .orElseThrow(() -> new NoSuchElementException(
                      "Category not found for id: " + categoryId));

      PlanCategoryAssociation association = new PlanCategoryAssociation();
      association.setPlanCategory(category);
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
    return planTypeRepository.findById(planTypeId)
            .orElseThrow(() -> new NoSuchElementException("PlanType not found for id: " + planTypeId));
  }

  @Named("mapDurationIdToEntity")
  protected PlanDuration mapDurationIdToEntity(int planDurationId) {
    return planDurationRepository.findById(planDurationId)
            .orElseThrow(() -> new NoSuchElementException("PlanDuration not found for id: " + planDurationId));
  }

  @Named("mapEquipmentIdToEntity")
  protected PlanEquipment mapEquipmentIdToEntity(int planEquipmentId) {
    return planEquipmentRepository.findById(planEquipmentId)
            .orElseThrow(() -> new NoSuchElementException("PlanEquipment not found for id: " + planEquipmentId));
  }

  @Named("mapExpertiseLevelIdToEntity")
  protected PlanExpertiseLevel mapExpertiseLevelIdToEntity(int planExpertiseLevelId) {
    return planExpertiseLevelRepository.findById(planExpertiseLevelId)
            .orElseThrow(() -> new NoSuchElementException("PlanExpertiseLevel not found for id: " + planExpertiseLevelId));
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

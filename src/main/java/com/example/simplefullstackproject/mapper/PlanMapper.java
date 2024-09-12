package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.PlanAdditionDto;
import com.example.simplefullstackproject.dto.PlanCategoryDto;
import com.example.simplefullstackproject.dto.PlanCategoryShortDto;
import com.example.simplefullstackproject.dto.PlanDto;
import com.example.simplefullstackproject.model.Plan;
import com.example.simplefullstackproject.model.PlanCategory;
import com.example.simplefullstackproject.model.PlanCategoryAssociation;
import com.example.simplefullstackproject.repository.PlanCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PlanMapper {
    @Autowired
    private PlanCategoryRepository planCategoryRepository;

    @Mapping(target = "categories", source = "planCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    public abstract PlanDto toDto(Plan plan);

    @Mapping(target = "planCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    public abstract Plan toEntity(PlanAdditionDto dto);

    public abstract PlanCategoryDto toCategoryDto(PlanCategory planCategory);

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
}

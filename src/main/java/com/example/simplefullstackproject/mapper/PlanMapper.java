package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.PlanCategoryDto;
import com.example.simplefullstackproject.dto.PlanDto;
import com.example.simplefullstackproject.model.Plan;
import com.example.simplefullstackproject.model.PlanCategory;
import com.example.simplefullstackproject.repository.PlanCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PlanMapper {
    @Autowired
    private PlanCategoryRepository planCategoryRepository;
    public abstract PlanDto toDto(Plan plan);

    @Mapping(target = "id", ignore = true)
    public abstract Plan toEntity(PlanDto dto);

    public abstract PlanCategoryDto toCategoryDto(PlanCategory planCategory);
}

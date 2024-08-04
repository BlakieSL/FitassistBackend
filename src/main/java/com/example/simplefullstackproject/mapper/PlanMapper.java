package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.PlanDto;
import com.example.simplefullstackproject.model.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    PlanDto toDto(Plan plan);

    @Mapping(target = "id", ignore = true)
    Plan toEntity(PlanDto dto);
}

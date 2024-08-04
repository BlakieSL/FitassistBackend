package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    RecipeDto toDto(Recipe recipe);

    @Mapping(target = "id", ignore = true)
    Recipe toEntity(RecipeDto dto);
}

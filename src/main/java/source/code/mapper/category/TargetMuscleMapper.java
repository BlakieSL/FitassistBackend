package source.code.mapper.category;

import org.mapstruct.*;
import source.code.dto.Request.category.CategoryCreateDto;
import source.code.dto.Request.category.CategoryUpdateDto;
import source.code.dto.Response.category.CategoryResponseDto;
import source.code.model.exercise.TargetMuscle;

@Mapper(componentModel = "spring")
public abstract class TargetMuscleMapper implements BaseMapper<TargetMuscle> {

    public abstract CategoryResponseDto toResponseDto(TargetMuscle targetMuscle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exerciseTargetMuscles", ignore = true)
    public abstract TargetMuscle toEntity(CategoryCreateDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exerciseTargetMuscles", ignore = true)
    public abstract void updateEntityFromDto(
            @MappingTarget TargetMuscle category, CategoryUpdateDto request);
}

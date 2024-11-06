package source.code.mapper.category;

import org.mapstruct.*;
import source.code.dto.Request.category.CategoryCreateDto;
import source.code.dto.Request.category.CategoryUpdateDto;
import source.code.dto.Response.category.CategoryResponseDto;
import source.code.model.food.FoodCategory;

@Mapper(componentModel = "spring")
public abstract class FoodCategoryMapper implements BaseMapper<FoodCategory> {
    public abstract CategoryResponseDto toResponseDto(FoodCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "foods", ignore = true)
    public abstract FoodCategory toEntity(CategoryCreateDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "foods", ignore = true)
    public abstract void updateEntityFromDto(
            @MappingTarget FoodCategory category, CategoryUpdateDto request);
}

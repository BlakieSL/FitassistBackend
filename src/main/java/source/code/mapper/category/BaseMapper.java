package source.code.mapper.category;

import org.mapstruct.MappingTarget;
import source.code.dto.Request.category.CategoryCreateDto;
import source.code.dto.Request.category.CategoryUpdateDto;
import source.code.dto.Response.category.CategoryResponseDto;

public interface BaseMapper<T> {
    CategoryResponseDto toResponseDto(T entity);

    T toEntity(CategoryCreateDto request);

    void updateEntityFromDto(@MappingTarget T entity, CategoryUpdateDto request);
}

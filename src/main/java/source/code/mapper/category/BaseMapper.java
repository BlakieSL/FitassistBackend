package source.code.mapper.category;

import org.mapstruct.MappingTarget;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.request.category.CategoryUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;

public interface BaseMapper<T> {

	CategoryResponseDto toResponseDto(T entity);

	T toEntity(CategoryCreateDto request);

	void updateEntityFromDto(@MappingTarget T entity, CategoryUpdateDto request);

}

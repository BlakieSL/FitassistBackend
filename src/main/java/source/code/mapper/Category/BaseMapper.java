package source.code.mapper.Category;

import org.mapstruct.MappingTarget;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;

public interface BaseMapper<T> {
  CategoryResponseDto toResponseDto(T entity);
  T toEntity(CategoryCreateDto request);
  void updateEntityFromDto(@MappingTarget T entity, CategoryUpdateDto request);
}

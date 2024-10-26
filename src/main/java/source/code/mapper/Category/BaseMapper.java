package source.code.mapper.Category;

import org.mapstruct.MappingTarget;
import source.code.dto.Request.Category.CategoryCreateDto;
import source.code.dto.Request.Category.CategoryUpdateDto;
import source.code.dto.Response.CategoryResponseDto;

public interface BaseMapper<T> {
  CategoryResponseDto toResponseDto(T entity);
  T toEntity(CategoryCreateDto request);
  void updateEntityFromDto(@MappingTarget T entity, CategoryUpdateDto request);
}

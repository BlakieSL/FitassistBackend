package source.code.mapper.category;

import org.mapstruct.*;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.request.category.CategoryUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.model.thread.ThreadCategory;

@Mapper(componentModel = "spring")
public abstract class ThreadCategoryMapper implements BaseMapper<ThreadCategory> {
    public abstract CategoryResponseDto toResponseDto(ThreadCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "threads", ignore = true)
    public abstract ThreadCategory toEntity(CategoryCreateDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "threads", ignore = true)
    public abstract void updateEntityFromDto(@MappingTarget ThreadCategory category, CategoryUpdateDto request);
}

package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.dto.response.other.MediaResponseDto;
import com.fitassist.backend.model.media.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaMapper {

	MediaResponseDto toDto(Media media, String imageUrl);

	@Mapping(target = "id", ignore = true)
	Media toEntity(MediaCreateDto dto, String imageName);

}

package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.dto.response.MediaResponseDto;
import com.fitassist.backend.model.media.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MediaMapper {

	public abstract MediaResponseDto toDto(Media media, String imageUrl);

	@Mapping(target = "id", ignore = true)
	public abstract Media toEntity(MediaCreateDto dto, String imageName);

}

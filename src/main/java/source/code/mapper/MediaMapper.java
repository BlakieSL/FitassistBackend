package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.model.media.Media;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaResponseDto toDto(Media media, String imageUrl);

    @Mapping(target = "id", ignore = true)
    Media toEntity(MediaCreateDto dto, String imageName);
}

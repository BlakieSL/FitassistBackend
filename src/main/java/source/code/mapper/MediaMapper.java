package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;
import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.exception.FileProcessingException;
import source.code.model.media.Media;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaResponseDto toDto(Media media, String imageUrl);

    @Mapping(target = "id", ignore = true)
    Media toEntity(MediaCreateDto dto, String imageName);
}

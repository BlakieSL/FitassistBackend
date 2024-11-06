package source.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;
import source.code.dto.Request.media.MediaCreateDto;
import source.code.dto.Response.MediaResponseDto;
import source.code.exception.FileProcessingException;
import source.code.model.media.Media;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    @Named("multipartFileToBytes")
    static byte[] multipartFileToBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process the image file", e);
        }
    }

    MediaResponseDto toDto(Media media);

    @Mapping(target = "image", source = "image", qualifiedByName = "multipartFileToBytes")
    @Mapping(target = "id", ignore = true)
    Media toEntity(MediaCreateDto dto);
}

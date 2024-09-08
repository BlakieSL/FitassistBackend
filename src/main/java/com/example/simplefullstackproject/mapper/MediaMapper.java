package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.AddMediaDto;
import com.example.simplefullstackproject.dto.MediaDto;
import com.example.simplefullstackproject.exception.FileProcessingException;
import com.example.simplefullstackproject.model.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaDto toDto(Media media);

    @Mapping(target = "image", source = "image", qualifiedByName = "multipartFileToBytes")
    @Mapping(target = "id", ignore = true)
    Media toEntity(AddMediaDto dto);

    @Named("multipartFileToBytes")
    static byte[] multipartFileToBytes(MultipartFile file)  {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process the image file", e);
        }
    }
}

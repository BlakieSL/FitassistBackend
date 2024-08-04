package com.example.simplefullstackproject.services.Mappers;

import com.example.simplefullstackproject.dtos.AddMediaDto;
import com.example.simplefullstackproject.dtos.MediaDto;
import com.example.simplefullstackproject.models.Media;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MediaDtoMapper {
    public MediaDto map(Media media) {
        return new MediaDto(
                media.getId(),
                media.getImage(),
                media.getType(),
                media.getParentId()
        );
    }

    public Media map(AddMediaDto request) throws IOException {
        Media media = new Media();
        media.setImage(request.getImage().getBytes());
        media.setType(request.getType());
        media.setParentId(request.getParentId());
        return media;
    }
}
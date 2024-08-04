package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.AddMediaDto;
import com.example.simplefullstackproject.dto.MediaDto;
import com.example.simplefullstackproject.model.Media;
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
package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.AddMediaDto;
import com.example.simplefullstackproject.Dtos.MediaDto;
import com.example.simplefullstackproject.Models.Media;
import com.example.simplefullstackproject.Repositories.MediaRepository;
import com.example.simplefullstackproject.Services.Mappers.MediaDtoMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MediaService {
    private final ValidationHelper validationHelper;
    private final MediaRepository mediaRepository;
    private final MediaDtoMapper mediaDtoMapper;

    public MediaService(
            final ValidationHelper validationHelper,
            final MediaRepository mediaRepository,
            final MediaDtoMapper mediaDtoMapper) {
        this.validationHelper = validationHelper;
        this.mediaRepository = mediaRepository;
        this.mediaDtoMapper = mediaDtoMapper;
    }

    @Transactional
    public List<MediaDto> findAllMediaForRecipe(Integer recipeId) {
        List<Media> mediaList = mediaRepository.findByParentId(recipeId);
        return mediaList.stream()
                .map(mediaDtoMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public MediaDto getMediaById(Integer mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId + " not found"));
        return mediaDtoMapper.map(media);
    }

    @Transactional
    public MediaDto saveMedia(AddMediaDto request) throws IOException {
        validationHelper.validate(request);

        Media savedMedia = mediaRepository.save(mediaDtoMapper.map(request));
        return mediaDtoMapper.map(savedMedia);
    }

    @Transactional
    public void removeMediaFromRecipe(Integer mediaId, Integer recipeId) {
        Media media = mediaRepository.findByIdAndParentId(mediaId, recipeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId +
                                " not found for recipe with id: " + recipeId));
        mediaRepository.delete(media);
    }
}

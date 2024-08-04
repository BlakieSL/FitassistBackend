package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.AddMediaDto;
import com.example.simplefullstackproject.dtos.MediaDto;
import com.example.simplefullstackproject.models.Media;
import com.example.simplefullstackproject.repositories.ExerciseRepository;
import com.example.simplefullstackproject.repositories.MediaRepository;
import com.example.simplefullstackproject.repositories.RecipeRepository;
import com.example.simplefullstackproject.services.Mappers.MediaDtoMapper;
import jakarta.transaction.Transactional;
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
    private final ExerciseRepository exerciseRepository;
    private final RecipeRepository recipeRepository;

    public MediaService(
            final ValidationHelper validationHelper,
            final MediaRepository mediaRepository,
            final MediaDtoMapper mediaDtoMapper,
            final ExerciseRepository exerciseRepository,
            final RecipeRepository recipeRepository) {
        this.validationHelper = validationHelper;
        this.mediaRepository = mediaRepository;
        this.mediaDtoMapper = mediaDtoMapper;
        this.exerciseRepository = exerciseRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public List<MediaDto> findAllMediaForParent(Integer parentId) {
        List<Media> mediaList = mediaRepository.findByParentId(parentId);
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

        if ("exercise".equalsIgnoreCase(request.getType())) {
            exerciseRepository
                    .findById(request.getParentId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Exercise with id: " + request.getParentId() + " not found"));
        } else if ("recipe".equalsIgnoreCase(request.getType())) {
            recipeRepository
                    .findById(request.getParentId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Recipe with id: " + request.getParentId() + " not found"));
        } else {
            throw new IllegalArgumentException(
                    "Invalid media type: " + request.getType());
        }

        Media savedMedia = mediaRepository.save(mediaDtoMapper.map(request));
        return mediaDtoMapper.map(savedMedia);
    }

    @Transactional
    public void removeMediaFromParent(Integer mediaId, Integer parentId) {
        Media media = mediaRepository
                .findByIdAndParentId(mediaId, parentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId +
                                " not found for parent with id: " + parentId));
        mediaRepository.delete(media);
    }
}

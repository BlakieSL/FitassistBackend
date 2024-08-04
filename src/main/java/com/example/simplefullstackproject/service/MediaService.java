package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.AddMediaDto;
import com.example.simplefullstackproject.dto.MediaDto;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.MediaMapper;
import com.example.simplefullstackproject.model.Media;
import com.example.simplefullstackproject.repository.ExerciseRepository;
import com.example.simplefullstackproject.repository.MediaRepository;
import com.example.simplefullstackproject.repository.RecipeRepository;
import com.example.simplefullstackproject.service.Mappers.MediaDtoMapper;
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
    private final MediaMapper mediaMapper;
    private final ExerciseRepository exerciseRepository;
    private final RecipeRepository recipeRepository;

    public MediaService(
            final ValidationHelper validationHelper,
            final MediaRepository mediaRepository,
            final MediaMapper mediaMapper,
            final ExerciseRepository exerciseRepository,
            final RecipeRepository recipeRepository) {
        this.validationHelper = validationHelper;
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.exerciseRepository = exerciseRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public List<MediaDto> findAllMediaForParent(Integer parentId) {
        List<Media> mediaList = mediaRepository.findByParentId(parentId);
        return mediaList.stream()
                .map(mediaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MediaDto getMediaById(Integer mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId + " not found"));
        return mediaMapper.toDto(media);
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

        Media savedMedia = mediaRepository.save(mediaMapper.toEntity(request));
        return mediaMapper.toDto(savedMedia);
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

package source.code.service;

import source.code.dto.request.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.MediaMapper;
import source.code.model.Media;
import source.code.repository.ExerciseRepository;
import source.code.repository.MediaRepository;
import source.code.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public MediaResponseDto createMedia(MediaCreateDto request) {
        validationHelper.validate(request);

        Media savedMedia = mediaRepository.save(mediaMapper.toEntity(request));
        return mediaMapper.toDto(savedMedia);
    }

    @Transactional
    public void deleteMedia(int mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId + " not found"));
        mediaRepository.delete(media);
    }

    public List<MediaResponseDto> getAllMediaForParent(int parentId, short parentType ) {
        List<Media> mediaList = mediaRepository.findByParentIdAndParentType(parentId, parentType);
        return mediaList.stream()
                .map(mediaMapper::toDto)
                .collect(Collectors.toList());
    }

    public MediaResponseDto getFirstMediaForParent(int parentId, short parentType) {
        Media media = mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType)
                .orElseThrow(() -> new NoSuchElementException(
                        "No media found with parentId: " + parentId + " and parentType: " + parentType));
        return mediaMapper.toDto(media);
    }

    public MediaResponseDto getMedia(int mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Media with id: " + mediaId + " not found"));
        return mediaMapper.toDto(media);
    }
}

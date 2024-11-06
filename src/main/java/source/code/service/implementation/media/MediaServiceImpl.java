package source.code.service.implementation.media;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.Request.media.MediaCreateDto;
import source.code.dto.Response.MediaResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.MediaMapper;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.media.MediaService;

import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {
    private final MediaMapper mediaMapper;
    private final RepositoryHelper repositoryHelper;
    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository,
                            MediaMapper mediaMapper,
                            RepositoryHelper repositoryHelper) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    @Transactional
    public MediaResponseDto createMedia(MediaCreateDto request) {
        Media savedMedia = mediaRepository.save(mediaMapper.toEntity(request));
        return mediaMapper.toDto(savedMedia);
    }

    @Override
    @Transactional
    public void deleteMedia(int mediaId) {
        Media media = find(mediaId);
        mediaRepository.delete(media);
    }

    @Override
    public List<MediaResponseDto> getAllMediaForParent(int parentId, short parentType) {
        return mediaRepository.findByParentIdAndParentType(parentId, parentType).stream()
                .map(mediaMapper::toDto)
                .toList();
    }

    @Override
    public MediaResponseDto getFirstMediaForParent(int parentId, short parentType) {
        Media media = mediaRepository
                .findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType)
                .orElseThrow(() -> RecordNotFoundException.of(Media.class, parentId, parentType));

        return mediaMapper.toDto(media);
    }

    @Override
    public MediaResponseDto getMedia(int mediaId) {
        Media media = find(mediaId);
        return mediaMapper.toDto(media);
    }

    private Media find(int mediaId) {
        return repositoryHelper.find(mediaRepository, Media.class, mediaId);
    }
}

package source.code.service.implementation.Media;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.MediaMapper;
import source.code.model.Media.Media;
import source.code.repository.MediaRepository;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Media.MediaService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

  @Transactional
  public MediaResponseDto createMedia(MediaCreateDto request) {
    Media savedMedia = mediaRepository.save(mediaMapper.toEntity(request));
    return mediaMapper.toDto(savedMedia);
  }

  @Transactional
  public void deleteMedia(int mediaId) {
    Media media = find(mediaId);
    mediaRepository.delete(media);
  }

  public List<MediaResponseDto> getAllMediaForParent(int parentId, short parentType) {
    return mediaRepository.findByParentIdAndParentType(parentId, parentType).stream()
            .map(mediaMapper::toDto)
            .toList();
  }

  public MediaResponseDto getFirstMediaForParent(int parentId, short parentType) {
    Media media = mediaRepository
            .findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType)
            .orElseThrow(() -> new RecordNotFoundException("Media", parentId, parentType));

    return mediaMapper.toDto(media);
  }

  public MediaResponseDto getMedia(int mediaId) {
    Media media = find(mediaId);
    return mediaMapper.toDto(media);
  }

  private Media find(int mediaId) {
    return repositoryHelper.find(mediaRepository, Media.class, mediaId);
  }
}

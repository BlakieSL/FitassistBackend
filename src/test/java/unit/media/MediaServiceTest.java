package unit.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.mapper.MediaMapper;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.implementation.media.MediaServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

    @Mock
    private MediaMapper mediaMapper;
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private MediaRepository mediaRepository;
    @InjectMocks
    private MediaServiceImpl mediaService;
    private Media media;
    private MediaCreateDto createDto;
    private MediaResponseDto responseDto;
    private int mediaId;
    private int parentId;
    private MediaConnectedEntity parentType;

    @BeforeEach
    void setUp() {
        media = new Media();
        createDto = new MediaCreateDto();
        responseDto = new MediaResponseDto();
        mediaId = 1;
        parentId = 1;
        parentType = MediaConnectedEntity.FOOD;
    }

    @Test
    void createMedia_shouldCreateMedia() {
        when(mediaMapper.toEntity(createDto)).thenReturn(media);
        when(mediaRepository.save(media)).thenReturn(media);
        when(mediaMapper.toDto(media)).thenReturn(responseDto);

        MediaResponseDto result = mediaService.createMedia(createDto);

        assertEquals(responseDto, result);
        verify(mediaRepository).save(media);
    }

    @Test
    void deleteMedia_shouldDeleteMedia() {
        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);

        mediaService.deleteMedia(mediaId);

        verify(mediaRepository).delete(media);
    }

    @Test
    void deleteMedia_shouldThrowExceptionWhenMediaNotFound() {
        when(repositoryHelper.find(mediaRepository, Media.class, mediaId))
                .thenThrow(RecordNotFoundException.of(Media.class, mediaId));

        assertThrows(RecordNotFoundException.class, () -> mediaService.deleteMedia(mediaId));

        verify(mediaRepository, never()).delete(media);
    }

    @Test
    void getAllMediaForParent_shouldReturnAllMediaForParent() {
        when(mediaRepository.findByParentIdAndParentType(parentId, parentType))
                .thenReturn(List.of(media));
        when(mediaMapper.toDto(media)).thenReturn(responseDto);

        List<MediaResponseDto> result = mediaService.getAllMediaForParent(parentId, parentType);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
    }

    @Test
    void getFirstMediaForParent_shouldReturnFirstMediaForParent() {
        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType))
                .thenReturn(java.util.Optional.of(media));
        when(mediaMapper.toDto(media)).thenReturn(responseDto);

        MediaResponseDto result = mediaService.getFirstMediaForParent(parentId, parentType);

        assertEquals(responseDto, result);
    }

    @Test
    void getFirstMediaForParent_shouldThrowExceptionWhenMediaNotFound() {
        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType))
                .thenReturn(java.util.Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> mediaService.getFirstMediaForParent(parentId, parentType));
    }

    @Test
    void getMedia_shouldReturnMedia() {
        when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
        when(mediaMapper.toDto(media)).thenReturn(responseDto);

        MediaResponseDto result = mediaService.getMedia(mediaId);

        assertEquals(responseDto, result);
    }

    @Test
    void getMedia_shouldThrowExceptionWhenMediaNotFound() {
        when(repositoryHelper.find(mediaRepository, Media.class, mediaId))
                .thenThrow(RecordNotFoundException.of(Media.class, mediaId));

        assertThrows(RecordNotFoundException.class, () -> mediaService.getMedia(mediaId));
    }
}
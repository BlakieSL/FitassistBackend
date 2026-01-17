package com.fitassist.backend.unit.media;

import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.dto.response.other.MediaResponseDto;
import com.fitassist.backend.event.events.Media.MediaDeleteEvent;
import com.fitassist.backend.event.events.Media.MediaUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.MediaMapper;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.implementation.media.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

	@Mock
	private AwsS3Service s3Service;

	@Mock
	private MediaMapper mediaMapper;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private MediaRepository mediaRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private MediaServiceImpl mediaService;

	private Media media;

	private MediaCreateDto createDto;

	private MediaResponseDto responseDto;

	private int mediaId;

	private int parentId;

	private MediaConnectedEntity parentType;

	private MultipartFile image;

	@BeforeEach
	void setUp() {
		media = new Media();
		createDto = new MediaCreateDto();
		responseDto = new MediaResponseDto();
		mediaId = 1;
		parentId = 1;
		parentType = MediaConnectedEntity.FOOD;
		image = mock(MultipartFile.class);
	}

	@Test
	void createMedia_shouldCreateMedia() throws IOException {
		byte[] imageBytes = "randomImageBytes".getBytes();
		String imageName = "randomImage.jpg";
		String imageUrl = "http://example.com/randomImage.jpg";
		createDto.setImage(image);

		when(image.getBytes()).thenReturn(imageBytes);
		when(s3Service.uploadImage(imageBytes)).thenReturn(imageName);
		when(mediaMapper.toEntity(createDto, imageName)).thenReturn(media);
		when(mediaRepository.save(media)).thenReturn(media);
		when(s3Service.getImage(imageName)).thenReturn(imageUrl);
		when(mediaMapper.toDto(media, imageUrl)).thenReturn(responseDto);

		MediaResponseDto result = mediaService.createMedia(createDto);

		assertEquals(responseDto, result);
		verify(mediaRepository).save(media);
		verify(applicationEventPublisher).publishEvent(any(MediaUpdateEvent.class));
	}

	@Test
	void deleteMedia_shouldDeleteMedia() {
		media.setImageName("randomImage.jpg");
		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		doNothing().when(s3Service).deleteImage(media.getImageName());

		mediaService.deleteMedia(mediaId);

		verify(mediaRepository).delete(media);
		verify(s3Service).deleteImage(media.getImageName());
		verify(applicationEventPublisher).publishEvent(any(MediaDeleteEvent.class));
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
		String imageUrl = "http://example.com/image.jpg";
		when(mediaRepository.findByParentIdAndParentType(parentId, parentType)).thenReturn(List.of(media));
		when(s3Service.getImage(media.getImageName())).thenReturn(imageUrl);
		when(mediaMapper.toDto(media, imageUrl)).thenReturn(responseDto);

		List<MediaResponseDto> result = mediaService.getAllMediaForParent(parentId, parentType);

		assertEquals(1, result.size());
		assertSame(responseDto, result.get(0));
	}

	@Test
	void getFirstMediaForParent_shouldReturnFirstMediaForParent() {
		String imageUrl = "http://example.com/image.jpg";
		when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType))
			.thenReturn(java.util.Optional.of(media));
		when(s3Service.getImage(media.getImageName())).thenReturn(imageUrl);
		when(mediaMapper.toDto(media, imageUrl)).thenReturn(responseDto);

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
		String imageUrl = "http://example.com/image.jpg";

		when(repositoryHelper.find(mediaRepository, Media.class, mediaId)).thenReturn(media);
		when(s3Service.getImage(media.getImageName())).thenReturn(imageUrl);
		when(mediaMapper.toDto(media, imageUrl)).thenReturn(responseDto);

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

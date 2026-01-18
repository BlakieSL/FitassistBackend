package com.fitassist.backend.service.implementation.media;

import com.fitassist.backend.dto.request.media.MediaCreateDto;
import com.fitassist.backend.dto.response.other.MediaResponseDto;
import com.fitassist.backend.event.events.Media.MediaDeleteEvent;
import com.fitassist.backend.event.events.Media.MediaUpdateEvent;
import com.fitassist.backend.exception.FileProcessingException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.MediaMapper;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.media.MediaService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {

	private final MediaMapper mediaMapper;

	private final RepositoryHelper repositoryHelper;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	private final ApplicationEventPublisher applicationEventPublisher;

	public MediaServiceImpl(MediaRepository mediaRepository, MediaMapper mediaMapper, RepositoryHelper repositoryHelper,
			AwsS3Service s3Service, ApplicationEventPublisher applicationEventPublisher) {
		this.mediaRepository = mediaRepository;
		this.mediaMapper = mediaMapper;
		this.repositoryHelper = repositoryHelper;
		this.s3Service = s3Service;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	@Transactional
	public MediaResponseDto createMedia(MediaCreateDto request) {
		deleteExistingUserMediaIfNeeded(request);

		byte[] imageBytes = multipartFileToBytes(request.getImage());
		String imageName = s3Service.uploadImage(imageBytes);

		Media savedMedia;
		try {
			savedMedia = mediaRepository.save(mediaMapper.toEntity(request, imageName));
			mediaRepository.flush();
		}
		catch (Exception e) {
			s3Service.deleteImage(imageName);
			throw e;
		}

		applicationEventPublisher.publishEvent(MediaUpdateEvent.of(this, savedMedia));

		String imageUrl = s3Service.getImage(imageName);

		return mediaMapper.toDto(savedMedia, imageUrl);
	}

	@Override
	@Transactional
	public void deleteMedia(int mediaId) {
		Media media = find(mediaId);
		String imageName = media.getImageName();

		mediaRepository.delete(media);
		mediaRepository.flush();

		s3Service.deleteImage(imageName);

		applicationEventPublisher.publishEvent(MediaDeleteEvent.of(this, media));
	}

	@Override
	public List<MediaResponseDto> getAllMediaForParent(int parentId, MediaConnectedEntity parentType) {
		return mediaRepository.findByParentIdAndParentType(parentId, parentType).stream().map(media -> {
			String imageUrl = s3Service.getImage(media.getImageName());
			return mediaMapper.toDto(media, imageUrl);
		}).toList();
	}

	@Override
	public MediaResponseDto getFirstMediaForParent(int parentId, MediaConnectedEntity parentType) {
		Media media = mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(parentId, parentType)
			.orElseThrow(() -> RecordNotFoundException.of(Media.class, parentId, parentType));

		String imageUrl = s3Service.getImage(media.getImageName());

		return mediaMapper.toDto(media, imageUrl);
	}

	@Override
	public MediaResponseDto getMedia(int mediaId) {
		Media media = find(mediaId);

		String imageUrl = s3Service.getImage(media.getImageName());

		return mediaMapper.toDto(media, imageUrl);
	}

	private Media find(int mediaId) {
		return repositoryHelper.find(mediaRepository, Media.class, mediaId);
	}

	private byte[] multipartFileToBytes(MultipartFile file) {
		try {
			return file.getBytes();
		}
		catch (IOException e) {
			throw new FileProcessingException("Failed to process the image file", e);
		}
	}

	private void deleteExistingUserMediaIfNeeded(MediaCreateDto request) {
		if (request.getParentType() == MediaConnectedEntity.USER) {
			mediaRepository
				.findFirstByParentIdAndParentTypeOrderByIdAsc(request.getParentId(), MediaConnectedEntity.USER)
				.ifPresent(existingMedia -> {
					String imageName = existingMedia.getImageName();
					mediaRepository.delete(existingMedia);
					mediaRepository.flush();
					s3Service.deleteImage(imageName);
					applicationEventPublisher.publishEvent(MediaDeleteEvent.of(this, existingMedia));
				});
		}
	}

}

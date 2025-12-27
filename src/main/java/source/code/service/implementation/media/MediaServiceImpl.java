package source.code.service.implementation.media;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import source.code.dto.request.media.MediaCreateDto;
import source.code.dto.response.MediaResponseDto;
import source.code.exception.FileProcessingException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.mapper.MediaMapper;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.media.MediaService;

@Service
public class MediaServiceImpl implements MediaService {

	private final MediaMapper mediaMapper;

	private final RepositoryHelper repositoryHelper;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public MediaServiceImpl(MediaRepository mediaRepository, MediaMapper mediaMapper, RepositoryHelper repositoryHelper,
							AwsS3Service s3Service) {
		this.mediaRepository = mediaRepository;
		this.mediaMapper = mediaMapper;
		this.repositoryHelper = repositoryHelper;
		this.s3Service = s3Service;
	}

	@Override
	@Transactional
	public MediaResponseDto createMedia(MediaCreateDto request) {
		deleteExistingUserMediaIfNeeded(request);

		byte[] imageBytes = multipartFileToBytes(request.getImage());
		String imageName = s3Service.uploadImage(imageBytes);

		Media savedMedia = mediaRepository.save(mediaMapper.toEntity(request, imageName));

		String imageUrl = s3Service.getImage(imageName);

		return mediaMapper.toDto(savedMedia, imageUrl);
	}

	@Override
	@Transactional
	public void deleteMedia(int mediaId) {
		Media media = find(mediaId);

		s3Service.deleteImage(media.getImageName());

		mediaRepository.delete(media);
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
		} catch (IOException e) {
			throw new FileProcessingException("Failed to process the image file", e);
		}
	}

	private void deleteExistingUserMediaIfNeeded(MediaCreateDto request) {
		if (request.getParentType() == MediaConnectedEntity.USER) {
			mediaRepository
				.findFirstByParentIdAndParentTypeOrderByIdAsc(request.getParentId(), MediaConnectedEntity.USER)
				.ifPresent(existingMedia -> {
					s3Service.deleteImage(existingMedia.getImageName());
					mediaRepository.delete(existingMedia);
				});
		}
	}

}

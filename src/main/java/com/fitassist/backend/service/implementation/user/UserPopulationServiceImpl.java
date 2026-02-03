package com.fitassist.backend.service.implementation.user;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.user.UserPopulationService;
import org.springframework.stereotype.Service;

@Service
public class UserPopulationServiceImpl implements UserPopulationService {

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public UserPopulationServiceImpl(MediaRepository mediaRepository, AwsS3Service s3Service) {
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Override
	public void populate(UserResponseDto user) {
		String imageUrl = getUserImageUrl(user.getId());
		user.setUserImageUrl(imageUrl);
	}

	@Override
	public void populate(AuthorDto author) {
		String imageUrl = getUserImageUrl(author.getId());
		author.setImageUrl(imageUrl);
	}

	private String getUserImageUrl(int userId) {
		return mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(userId, MediaConnectedEntity.USER)
			.map(media -> s3Service.getImage(media.getImageName()))
			.orElse(null);
	}

}

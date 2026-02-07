package com.fitassist.backend.service.implementation.helpers;

import com.fitassist.backend.dto.pojo.MediaImagesDto;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.helpers.ImageUrlPopulationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageUrlPopulationServiceImpl implements ImageUrlPopulationService {

	private final AwsS3Service s3Service;

	public ImageUrlPopulationServiceImpl(AwsS3Service s3Service) {
		this.s3Service = s3Service;
	}

	@Override
	public void populateImageUrls(MediaImagesDto images) {
		if (images == null || images.getImageNames() == null) {
			return;
		}

		List<String> urls = images.getImageNames().stream().map(s3Service::getImage).toList();
		images.setImageUrls(urls);
	}

}

package source.code.service.implementation.helpers;

import java.util.List;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.MediaImagesDto;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.ImageUrlPopulationService;

@Service
public class ImageUrlPopulationServiceImpl implements ImageUrlPopulationService {

	private final AwsS3Service s3Service;

	public ImageUrlPopulationServiceImpl(AwsS3Service s3Service) {
		this.s3Service = s3Service;
	}

	@Override
	public void populateImageUrls(MediaImagesDto images) {
		if (images == null || images.getImageNames() == null)
			return;

		List<String> urls = images.getImageNames().stream().map(s3Service::getImage).toList();
		images.setImageUrls(urls);
	}

}

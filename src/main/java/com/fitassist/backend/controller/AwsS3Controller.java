package com.fitassist.backend.controller;

import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/s3")
public class AwsS3Controller {

	private final AwsS3Service awsS3Service;

	public AwsS3Controller(AwsS3Service awsS3Service) {
		this.awsS3Service = awsS3Service;
	}

	@GetMapping("/{imageName}")
	public String getImageUrl(@PathVariable String imageName) {
		return awsS3Service.getImage(imageName);
	}

}

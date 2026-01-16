package com.fitassist.backend.service.declaration.aws;

public interface AwsS3Service {

	String uploadImage(byte[] imageBytes);

	String getImage(String imageName);

	void deleteImage(String imageName);

}

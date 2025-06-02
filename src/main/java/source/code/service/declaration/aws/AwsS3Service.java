package source.code.service.declaration.aws;

public interface AwsS3Service {
    String uploadImage(byte[] imageBytes);
    String getImage(String imageName);
}

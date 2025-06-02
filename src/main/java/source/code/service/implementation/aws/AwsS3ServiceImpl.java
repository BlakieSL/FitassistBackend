package source.code.service.implementation.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import source.code.service.declaration.aws.AwsS3Service;

import java.util.UUID;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s3.bucket.name}")
    private String bucketName;

    public AwsS3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String uploadImage(byte[] imageBytes) {
        String uniqueFileName = UUID.randomUUID() + ".jpg";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(imageBytes));

        return uniqueFileName;
    }

    @Override
    public String getImage(String imageName) {
        return generatePresignedUrl(imageName);
    }

    private String generatePresignedUrl(String keyName) {
        return s3Presigner.presignGetObject(builder -> builder
                .signatureDuration(java.time.Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequestBuilder -> getObjectRequestBuilder
                        .bucket(bucketName)
                        .key(keyName)))
                .url().toString();
    }
}

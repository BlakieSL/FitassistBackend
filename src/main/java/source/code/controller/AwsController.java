package source.code.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.service.declaration.aws.AwsS3Service;


@RestController
@RequestMapping("/api/s3")
public class AwsController {
    private final AwsS3Service awsS3Service;

    public AwsController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @GetMapping("/{imageName}")
    public String getImageUrl(@PathVariable String imageName) {
        System.out.println("Fetching image URL for: " + imageName);
        return awsS3Service.getImage(imageName);
    }
}

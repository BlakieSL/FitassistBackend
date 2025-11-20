package source.code.service.implementation.helpers;

import org.springframework.stereotype.Service;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.ImageUrlPopulationService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class ImageUrlPopulationServiceImpl implements ImageUrlPopulationService {
    private final AwsS3Service s3Service;

    public ImageUrlPopulationServiceImpl(AwsS3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public <T> void populateAuthorAndEntityImagesForList(List<T> dtos,
                                                          Function<T, String> authorImageNameGetter,
                                                          BiConsumer<T, String> authorUrlSetter,
                                                          Function<T, String> entityImageNameGetter,
                                                          BiConsumer<T, String> entityUrlSetter) {
        dtos.forEach(dto -> {
            populateImageUrl(authorImageNameGetter.apply(dto), url -> authorUrlSetter.accept(dto, url));
            populateImageUrl(entityImageNameGetter.apply(dto), url -> entityUrlSetter.accept(dto, url));
        });
    }

    @Override
    public <T> void populateAuthorImageForList(List<T> dtos,
                                                Function<T, String> authorImageNameGetter,
                                                BiConsumer<T, String> authorUrlSetter) {
        dtos.forEach(dto ->
            populateImageUrl(authorImageNameGetter.apply(dto), url -> authorUrlSetter.accept(dto, url))
        );
    }

    private void populateImageUrl(String imageName, Consumer<String> urlSetter) {
        if (imageName != null) {
            urlSetter.accept(s3Service.getImage(imageName));
        }
    }
}

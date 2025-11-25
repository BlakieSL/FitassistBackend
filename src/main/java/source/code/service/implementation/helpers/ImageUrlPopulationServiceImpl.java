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
    public <T> void populateAuthorAndEntityImages(T dto,
                                                   Function<T, String> authorImageNameGetter,
                                                   BiConsumer<T, String> authorUrlSetter,
                                                   Function<T, String> entityImageNameGetter,
                                                   BiConsumer<T, String> entityUrlSetter) {
        populateImageUrl(authorImageNameGetter.apply(dto), url -> authorUrlSetter.accept(dto, url));
        populateImageUrl(entityImageNameGetter.apply(dto), url -> entityUrlSetter.accept(dto, url));
    }

    @Override
    public <T> void populateAuthorImage(T dto,
                                         Function<T, String> authorImageNameGetter,
                                         BiConsumer<T, String> authorUrlSetter) {
        populateImageUrl(authorImageNameGetter.apply(dto), url -> authorUrlSetter.accept(dto, url));
    }

    @Override
    public <T, M> void populateFirstImageFromMediaList(T dto,
                                                        List<M> mediaList,
                                                        Function<M, String> imageNameExtractor,
                                                        BiConsumer<T, String> imageNameSetter,
                                                        BiConsumer<T, String> imageUrlSetter) {
        if (mediaList != null && !mediaList.isEmpty()) {
            String imageName = imageNameExtractor.apply(mediaList.getFirst());
            if (imageName != null) {
                imageNameSetter.accept(dto, imageName);
                imageUrlSetter.accept(dto, s3Service.getImage(imageName));
            }
        }
    }

    private void populateImageUrl(String imageName, Consumer<String> urlSetter) {
        if (imageName != null) {
            urlSetter.accept(s3Service.getImage(imageName));
        }
    }
}

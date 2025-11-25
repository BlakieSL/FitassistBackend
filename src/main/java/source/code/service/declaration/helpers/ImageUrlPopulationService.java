package source.code.service.declaration.helpers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ImageUrlPopulationService {
    <T> void populateAuthorAndEntityImages(T dto,
                                            Function<T, String> authorImageNameGetter,
                                            BiConsumer<T, String> authorUrlSetter,
                                            Function<T, String> entityImageNameGetter,
                                            BiConsumer<T, String> entityUrlSetter);

    <T> void populateAuthorImage(T dto,
                                  Function<T, String> authorImageNameGetter,
                                  BiConsumer<T, String> authorUrlSetter);

    <T, M> void populateFirstImageFromMediaList(T dto,
                                                 List<M> mediaList,
                                                 Function<M, String> imageNameExtractor,
                                                 BiConsumer<T, String> imageNameSetter,
                                                 BiConsumer<T, String> imageUrlSetter);
}

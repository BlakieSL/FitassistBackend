package source.code.service.declaration.helpers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ImageUrlPopulationService {
    <T> void populateAuthorAndEntityImagesForList(List<T> dtos,
                                                   Function<T, String> authorImageNameGetter,
                                                   BiConsumer<T, String> authorUrlSetter,
                                                   Function<T, String> entityImageNameGetter,
                                                   BiConsumer<T, String> entityUrlSetter);

    <T> void populateAuthorImageForList(List<T> dtos,
                                         Function<T, String> authorImageNameGetter,
                                         BiConsumer<T, String> authorUrlSetter);
}

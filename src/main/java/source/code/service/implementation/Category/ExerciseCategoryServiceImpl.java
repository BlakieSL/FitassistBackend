package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.mapper.Exercise.ExerciseCategoryMapper;
import source.code.model.Exercise.ExerciseCategory;
import source.code.repository.ExerciseCategoryRepository;
import source.code.service.declaration.Category.CategoryService;


@Service("exerciseCategoryService")
public class ExerciseCategoryServiceImpl
        extends GenericCategoryService<ExerciseCategory>
        implements CategoryService {

  protected ExerciseCategoryServiceImpl(ValidationService validationService,
                                        JsonPatchServiceImpl jsonPatchServiceImpl,
                                        ApplicationEventPublisher applicationEventPublisher,
                                        CacheManager cacheManager,
                                        ExerciseCategoryRepository repository,
                                        ExerciseCategoryMapper mapper) {
    super(validationService,
            jsonPatchServiceImpl,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

package source.code.service.Implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.ExerciseCategoryMapper;
import source.code.model.Exercise.ExerciseCategory;
import source.code.repository.ExerciseCategoryRepository;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.Declaration.Category.CategoryService;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;


@Service("exerciseCategoryService")
public class ExerciseCategoryServiceImpl
        extends GenericCategoryService<ExerciseCategory>
        implements CategoryService {

  protected ExerciseCategoryServiceImpl(ValidationService validationService,
                                        JsonPatchService jsonPatchService,
                                        CategoryCacheKeyGenerator<ExerciseCategory> cacheKeyGenerator,
                                        ApplicationEventPublisher applicationEventPublisher,
                                        CacheManager cacheManager,
                                        ExerciseCategoryRepository repository,
                                        ExerciseCategoryMapper mapper) {
    super(validationService,
            jsonPatchService,
            cacheKeyGenerator,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

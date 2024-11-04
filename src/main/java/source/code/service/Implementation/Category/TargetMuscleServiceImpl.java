package source.code.service.Implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.TargetMuscleMapper;
import source.code.model.Exercise.TargetMuscle;
import source.code.repository.TargetMuscleRepository;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.Declaration.Category.CategoryService;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;


@Service("targetMuscleService")
public class TargetMuscleServiceImpl
        extends GenericCategoryService<TargetMuscle>
        implements CategoryService {

    protected TargetMuscleServiceImpl(ValidationService validationService,
                                      JsonPatchService jsonPatchService,
                                      CategoryCacheKeyGenerator<TargetMuscle> cacheKeyGenerator,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      TargetMuscleRepository repository,
                                      TargetMuscleMapper mapper) {
        super(validationService,
                jsonPatchService,
                cacheKeyGenerator,
                applicationEventPublisher,
                cacheManager,
                repository,
                mapper);
    }
}

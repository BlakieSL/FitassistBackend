package source.code.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.category.TargetMuscleMapper;
import source.code.model.exercise.TargetMuscle;
import source.code.repository.TargetMuscleRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;


@Service("targetMuscleService")
public class TargetMuscleServiceImpl
        extends GenericCategoryService<TargetMuscle>
        implements CategoryService {

    private final TargetMuscleRepository targetMuscleRepository;

    protected TargetMuscleServiceImpl(ValidationService validationService,
                                      JsonPatchService jsonPatchService,
                                      CategoryCacheKeyGenerator<TargetMuscle> cacheKeyGenerator,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      TargetMuscleRepository targetMuscleRepository,
                                      TargetMuscleMapper mapper) {
        super(validationService,
                jsonPatchService,
                cacheKeyGenerator,
                applicationEventPublisher,
                cacheManager,
                targetMuscleRepository,
                mapper);
        this.targetMuscleRepository = targetMuscleRepository;
    }

    @Override
    protected boolean hasAssociatedEntities(int categoryId) {
        return targetMuscleRepository.existsByIdAndExerciseTargetMusclesIsNotEmpty(categoryId);
    }
}

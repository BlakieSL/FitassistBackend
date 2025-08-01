package source.code.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.category.FoodCategoryMapper;
import source.code.model.food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

@Service("foodCategoryService")
public class FoodCategoryServiceImpl
        extends GenericCategoryService<FoodCategory>
        implements CategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    protected FoodCategoryServiceImpl(ValidationService validationService,
                                      JsonPatchService jsonPatchService,
                                      CategoryCacheKeyGenerator<FoodCategory> cacheKeyGenerator,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      FoodCategoryRepository foodCategoryRepository,
                                      FoodCategoryMapper mapper) {
        super(validationService,
                jsonPatchService,
                cacheKeyGenerator,
                applicationEventPublisher,
                cacheManager,
                foodCategoryRepository,
                mapper);
        this.foodCategoryRepository = foodCategoryRepository;
    }

    @Override
    protected boolean hasAssociatedEntities(int categoryId) {
        return foodCategoryRepository.existsByIdAndFoodsIsNotEmpty(categoryId);
    }

    @Override
    protected Class<FoodCategory> getEntityClass() {
        return FoodCategory.class;
    }
}

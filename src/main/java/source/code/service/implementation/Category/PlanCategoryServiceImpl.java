package source.code.service.implementation.Category;

import org.springframework.stereotype.Service;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Plan.PlanCategoryMapper;
import source.code.model.Plan.PlanCategory;
import source.code.repository.PlanCategoryRepository;
import source.code.service.declaration.Category.CategoryService;

@Service("planCategoryService")
public class PlanCategoryServiceImpl
        extends GenericCategoryService<PlanCategory>
        implements CategoryService {
  protected PlanCategoryServiceImpl(ValidationServiceImpl validationServiceImpl,
                                    JsonPatchServiceImpl jsonPatchServiceImpl,
                                    PlanCategoryRepository repository,
                                    PlanCategoryMapper mapper) {
    super(validationServiceImpl, jsonPatchServiceImpl, repository, mapper);
  }
}

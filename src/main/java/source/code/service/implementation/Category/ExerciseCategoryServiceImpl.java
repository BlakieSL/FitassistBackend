package source.code.service.implementation.Category;

import org.springframework.stereotype.Service;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Exercise.ExerciseCategoryMapper;
import source.code.model.Exercise.ExerciseCategory;
import source.code.repository.ExerciseCategoryRepository;
import source.code.service.declaration.Category.CategoryService;


@Service("exerciseCategoryService")
public class ExerciseCategoryServiceImpl
        extends GenericCategoryService<ExerciseCategory>
        implements CategoryService {

  protected ExerciseCategoryServiceImpl(ValidationServiceImpl validationServiceImpl,
                                        JsonPatchServiceImpl jsonPatchServiceImpl,
                                        ExerciseCategoryRepository repository,
                                        ExerciseCategoryMapper mapper) {
    super(validationServiceImpl, jsonPatchServiceImpl, repository, mapper);
  }
}

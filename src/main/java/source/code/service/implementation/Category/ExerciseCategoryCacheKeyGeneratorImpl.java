package source.code.service.implementation.Category;

import jdk.jfr.Category;
import org.springframework.stereotype.Service;
import source.code.model.Exercise.ExerciseCategory;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;

@Service
public class ExerciseCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ExerciseCategory> {
  @Override
  public String generateCacheKey() {
    return "exerciseCategories";
  }
}

package source.code.service.Implementation.Category;

import org.springframework.stereotype.Service;
import source.code.model.Exercise.ExerciseCategory;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;

@Service
public class ExerciseCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ExerciseCategory> {
  @Override
  public String generateCacheKey() {
    return "exerciseCategories";
  }
}

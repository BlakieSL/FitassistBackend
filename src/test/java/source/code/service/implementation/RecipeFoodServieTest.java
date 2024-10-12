package source.code.service.implementation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;

@ExtendWith(MockitoExtension.class)
public class RecipeFoodServieTest {
  private RecipeFoodRepository recipeFoodRepository;
  private FoodRepository foodRepository;
  private RecipeRepository recipeRepository;
  private JsonPatchHelper jsonPatchHelper;
}

package source.code.mapper.Search;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import source.code.dto.response.Other.SearchResultDto;
import source.code.helper.enumerators.EntityType;
import source.code.search.document.*;

@Mapper(componentModel = "spring")
public abstract class SearchMapper {
  public SearchResultDto<FoodDocument> toFoodSearchResult(FoodDocument foodDocument) {
    return SearchResultDto.create(EntityType.FOOD, foodDocument);
  }

  public SearchResultDto<ActivityDocument> toActivitySearchResult(ActivityDocument activityDocument) {
    return SearchResultDto.create(EntityType.ACTIVITY, activityDocument);
  }

  public SearchResultDto<ExerciseDocument> toExerciseSearchResult(ExerciseDocument exerciseDocument) {
    return SearchResultDto.create(EntityType.EXERCISE, exerciseDocument);
  }

  public SearchResultDto<RecipeDocument> toRecipeSearchResult(RecipeDocument recipeDocument) {
    return SearchResultDto.create(EntityType.RECIPE, recipeDocument);
  }

  public SearchResultDto<PlanDocument> toPlanSearchResult(PlanDocument planDocument) {
    return SearchResultDto.create(EntityType.PLAN, planDocument);
  }
}

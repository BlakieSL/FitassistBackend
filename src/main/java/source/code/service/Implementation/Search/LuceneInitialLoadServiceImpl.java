package source.code.service.Implementation.Search;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import source.code.helper.Search.IndexedEntity;
import source.code.service.Declaration.Activity.ActivityService;
import source.code.service.Declaration.Exercise.ExerciseService;
import source.code.service.Declaration.Food.FoodService;
import source.code.service.Declaration.Plan.PlanService;
import source.code.service.Declaration.Recipe.RecipeService;
import source.code.service.Declaration.Search.LuceneIndexService;
import source.code.service.Declaration.Search.LuceneInitialLoadService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LuceneInitialLoadServiceImpl implements LuceneInitialLoadService {
  private final LuceneIndexService luceneIndexService;
  private final FoodService foodService;
  private final ActivityService activityService;
  private final ExerciseService exerciseService;
  private final RecipeService recipeService;
  private final PlanService planService;

  public LuceneInitialLoadServiceImpl(LuceneIndexService luceneIndexService,
                                      FoodService foodService,
                                      ActivityService activityService,
                                      ExerciseService exerciseService,
                                      RecipeService recipeService,
                                      PlanService planService) {
    this.luceneIndexService = luceneIndexService;
    this.foodService = foodService;
    this.activityService = activityService;
    this.exerciseService = exerciseService;
    this.recipeService = recipeService;
    this.planService = planService;
  }


  @Override
  @PostConstruct
  public void indexAll() {
    List<IndexedEntity> allEntities = Stream.of(
                    foodService.getAllFoodEntities(),
                    activityService.getAllActivityEntities(),
                    exerciseService.getAllExerciseEntities(),
                    recipeService.getAllRecipeEntities(),
                    planService.getAllPlanEntities())
            .flatMap(list -> list.stream().map(entity -> (IndexedEntity) entity))
            .collect(Collectors.toList());

    luceneIndexService.indexEntities(allEntities);
  }
}

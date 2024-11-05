package source.code.service.implementation.search;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import source.code.helper.Search.IndexedEntity;
import source.code.service.declaration.activity.ActivityService;
import source.code.service.declaration.exercise.ExerciseService;
import source.code.service.declaration.food.FoodService;
import source.code.service.declaration.plan.PlanService;
import source.code.service.declaration.recipe.RecipeService;
import source.code.service.declaration.search.LuceneIndexService;
import source.code.service.declaration.search.LuceneInitialLoadService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LuceneInitialLoadServiceImpl implements LuceneInitialLoadService {
    private static final String PATH = "src/main/resources/lucene-index";
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
        clearIndexDirectory();

        List<IndexedEntity> allEntities = Stream.of(
                        foodService.getAllFoodEntities(),
                        activityService.getAllActivityEntities(),
                        exerciseService.getAllExerciseEntities(),
                        recipeService.getAllRecipeEntities(),
                        planService.getAllPlanEntities())
                .flatMap(list -> list.stream().map(entity -> (IndexedEntity) entity))
                .toList();

        luceneIndexService.indexEntities(allEntities);
    }

    private void clearIndexDirectory() {
        Path indexPath = Paths.get(PATH);
        if (Files.exists(indexPath)) {
            try (Stream<Path> files = Files.walk(indexPath)) {
                files.sorted(Comparator.reverseOrder())
                        .forEach(this::deleteFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

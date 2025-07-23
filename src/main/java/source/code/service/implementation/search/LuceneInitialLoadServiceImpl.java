package source.code.service.implementation.search;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import source.code.helper.search.IndexedEntity;
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
@ConditionalOnProperty(name = "lucene.enabled", havingValue = "true", matchIfMissing = true)
public class LuceneInitialLoadServiceImpl implements LuceneInitialLoadService {
    private static final String PATH = "src/main/resources/lucene-index";
    private final LuceneIndexService luceneIndexService;
    private final FoodService foodService;
    private final ActivityService activityService;
    private final ExerciseService exerciseService;
    private final RecipeService recipeService;
    private final PlanService planService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneInitialLoadServiceImpl.class);

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

    @PreDestroy
    public void clearIndexOnShutdown() {
        LOGGER.info("Application shutting down. Clearing Lucene index directory");
        clearIndexDirectory();
        LOGGER.info("Lucene index directory cleared.");
    }

    @PostConstruct
    @Override
    public void indexAll() {
        clearIndexDirectory();

        List<IndexedEntity> allEntities = List.of();

        try {
            allEntities = Stream.of(
                    foodService.getAllFoodEntities(),
                    activityService.getAllActivityEntities(),
                    exerciseService.getAllExerciseEntities(),
                    recipeService.getAllRecipeEntities(),
                    planService.getAllPlanEntities()
            ).flatMap(List::stream)
                    .map(entity -> (IndexedEntity) entity)
                    .toList();
        } catch (Exception ex) {
            LOGGER.error("DB was not initialized yet, skipping indexing", ex);
        }

        luceneIndexService.indexEntities(allEntities);
    }

    @Override
    public void clearIndexDirectory() {
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

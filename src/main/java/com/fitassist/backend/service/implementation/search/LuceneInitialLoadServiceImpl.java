package com.fitassist.backend.service.implementation.search;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import com.fitassist.backend.model.IndexedEntity;
import com.fitassist.backend.service.declaration.activity.ActivityService;
import com.fitassist.backend.service.declaration.exercise.ExerciseService;
import com.fitassist.backend.service.declaration.food.FoodService;
import com.fitassist.backend.service.declaration.plan.PlanService;
import com.fitassist.backend.service.declaration.recipe.RecipeService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import com.fitassist.backend.service.declaration.search.LuceneInitialLoadService;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(LuceneInitialLoadServiceImpl.class);

	@Value("${lucene.enabled}")
	private Boolean luceneEnabled;

	public LuceneInitialLoadServiceImpl(LuceneIndexService luceneIndexService, FoodService foodService,
			ActivityService activityService, ExerciseService exerciseService, RecipeService recipeService,
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

	@EventListener(ApplicationReadyEvent.class)
	@Override
	public void indexAll() {
		if (luceneEnabled) {
			try {
				clearIndexDirectory();

				List<IndexedEntity> allEntities = Stream
					.of(foodService.getAllFoodEntities(), activityService.getAllActivityEntities(),
							exerciseService.getAllExerciseEntities(), recipeService.getAllRecipeEntities(),
							planService.getAllPlanEntities())
					.flatMap(List::stream)
					.map(entity -> (IndexedEntity) entity)
					.toList();

				luceneIndexService.indexEntities(allEntities);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clearIndexDirectory() {
		Path indexPath = Paths.get(PATH);
		if (Files.exists(indexPath)) {
			try (Stream<Path> files = Files.walk(indexPath)) {
				files.sorted(Comparator.reverseOrder()).forEach(this::deleteFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteFile(Path path) {
		try {
			Files.delete(path);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}

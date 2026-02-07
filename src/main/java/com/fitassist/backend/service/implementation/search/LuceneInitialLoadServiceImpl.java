package com.fitassist.backend.service.implementation.search;

import com.fitassist.backend.model.IndexedEntity;
import com.fitassist.backend.service.declaration.activity.ActivityService;
import com.fitassist.backend.service.declaration.exercise.ExerciseService;
import com.fitassist.backend.service.declaration.food.FoodService;
import com.fitassist.backend.service.declaration.plan.PlanService;
import com.fitassist.backend.service.declaration.recipe.RecipeService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import com.fitassist.backend.service.declaration.search.LuceneInitialLoadService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class LuceneInitialLoadServiceImpl implements LuceneInitialLoadService {

	private static final String PATH = "src/main/resources/lucene-index";

	private final LuceneIndexService luceneIndexService;

	private final FoodService foodService;

	private final ActivityService activityService;

	private final ExerciseService exerciseService;

	private final RecipeService recipeService;

	private final PlanService planService;

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
		log.info("Application shutting down. Clearing Lucene index directory");
		clearIndexDirectory();
		log.info("Lucene index directory cleared.");
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
				log.error("Failed to index entities", e);
			}
		}
	}

	@Override
	public void clearIndexDirectory() {
		File indexDir = new File(PATH);
		if (indexDir.exists()) {
			try {
				FileUtils.cleanDirectory(indexDir);
			}
			catch (IOException e) {
				log.error("Failed to clear index directory", e);
			}
		}
	}

}

package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.events.Exercise.ExerciseCreateEvent;
import com.fitassist.backend.event.events.Exercise.ExerciseDeleteEvent;
import com.fitassist.backend.event.events.Exercise.ExerciseUpdateEvent;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExerciseListener {

	private final CacheService cacheService;

	private final LuceneIndexService luceneService;

	public ExerciseListener(CacheService cacheService, LuceneIndexService luceneService) {
		this.cacheService = cacheService;
		this.luceneService = luceneService;
	}

	@EventListener
	public void handleExerciseCreate(ExerciseCreateEvent event) {
		Exercise exercise = event.getExercise();

		luceneService.addEntity(exercise);
	}

	@EventListener
	public void handleExerciseUpdate(ExerciseUpdateEvent event) {
		Exercise exercise = event.getExercise();

		clearCache(exercise);
		luceneService.updateEntity(exercise);
	}

	@EventListener
	public void handleExerciseDelete(ExerciseDeleteEvent event) {
		Exercise exercise = event.getExercise();

		clearCache(exercise);
		luceneService.deleteEntity(exercise);
	}

	private void clearCache(Exercise exercise) {
		cacheService.evictCache(CacheNames.EXERCISES, exercise.getId());
	}

}

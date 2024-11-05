package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Exercise.ExerciseCreateEvent;
import source.code.event.events.Exercise.ExerciseDeleteEvent;
import source.code.event.events.Exercise.ExerciseUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;
import source.code.service.declaration.cache.CacheService;
import source.code.service.declaration.search.LuceneIndexService;

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

        clearCommonCache(exercise);
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
        clearCommonCache(exercise);
    }

    private void clearCommonCache(Exercise exercise) {
        cacheService.clearCache(CacheNames.ALL_EXERCISES);
        clearExercisesByCategoryCache(exercise);
    }

    private void clearExercisesByCategoryCache(Exercise exercise) {
        if (exercise.getExerciseTargetMuscles() != null) {
            for (ExerciseTargetMuscle association : exercise.getExerciseTargetMuscles()) {
                cacheService.evictCache(CacheNames.EXERCISES_BY_CATEGORY,
                        association.getTargetMuscle().getId());
            }
        }
    }
}

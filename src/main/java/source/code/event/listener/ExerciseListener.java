package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Exercise.ExerciseCreateEvent;
import source.code.event.events.Exercise.ExerciseDeleteEvent;
import source.code.event.events.Exercise.ExerciseUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.helper.Enum.ExerciseField;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseCategoryAssociation;
import source.code.service.Declaration.Cache.CacheService;
import source.code.service.Declaration.Search.LuceneIndexService;

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
    clearExercisesByFieldCache(exercise);
    clearExercisesByCategoryCache(exercise);
  }

  private void clearExercisesByFieldCache(Exercise exercise) {
    cacheService.evictCache(CacheNames.EXERCISES_BY_FIELD,
            ExerciseField.EXPERTISE_LEVEL.name() + exercise.getExpertiseLevel().getId());

    cacheService.evictCache(CacheNames.EXERCISES_BY_FIELD,
            ExerciseField.FORCE_TYPE.name() + exercise.getForceType().getId());

    cacheService.evictCache(CacheNames.EXERCISES_BY_FIELD,
            ExerciseField.MECHANICS_TYPE.name() + exercise.getMechanicsType().getId());

    cacheService.evictCache(CacheNames.EXERCISES_BY_FIELD,
            ExerciseField.EQUIPMENT.name() + exercise.getExerciseEquipment().getId());

    cacheService.evictCache(CacheNames.EXERCISES_BY_FIELD,
            ExerciseField.TYPE.name() + exercise.getExerciseType().getId());
  }

  private void clearExercisesByCategoryCache(Exercise exercise) {
    if (exercise.getExerciseCategoryAssociations() != null) {
      for (ExerciseCategoryAssociation association : exercise.getExerciseCategoryAssociations()) {
        cacheService.evictCache(CacheNames.EXERCISES_BY_CATEGORY,
                association.getExerciseCategory().getId());
      }
    }
  }
}

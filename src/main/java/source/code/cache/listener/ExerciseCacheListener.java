package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Exercise.ExerciseCreateEvent;
import source.code.cache.event.Exercise.ExerciseDeleteEvent;
import source.code.cache.event.Exercise.ExerciseUpdateEvent;
import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.ExerciseUpdateDto;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseCategoryAssociation;

@Component
public class ExerciseCacheListener {
  private final CacheManager cacheManager;

  public ExerciseCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleExerciseCreate(ExerciseCreateEvent event) {
    Exercise exercise = event.getExercise();

    cacheManager.getCache("allExercises").clear();
    clearExercisesByFieldCache(exercise);
    clearExercisesByCategoryCache(exercise);
  }

  @EventListener
  public void handleExerciseUpdate(ExerciseUpdateEvent event) {
    Exercise exercise = event.getExercise();
    clearCache(exercise);
  }

  @EventListener
  public void handleExerciseDelete(ExerciseDeleteEvent event) {
    Exercise exercise = event.getExercise();
    clearCache(exercise);
  }

  private void clearCache(Exercise exercise) {
    cacheManager.getCache("exercises").evict(exercise.getId());
    cacheManager.getCache("allExercises").clear();
    clearExercisesByFieldCache(exercise);
    clearExercisesByCategoryCache(exercise);
  }

  private void clearExercisesByFieldCache(Exercise exercise) {
    cacheManager.getCache("exercisesByField")
            .evict("EXPERTISE_LEVEL_" + exercise.getExpertiseLevel().getId());
    cacheManager.getCache("exercisesByField")
            .evict("FORCE_TYPE_" + exercise.getForceType().getId());
    cacheManager.getCache("exercisesByField")
            .evict("MECHANICS_TYPE_" + exercise.getMechanicsType().getId());
    cacheManager.getCache("exercisesByField")
            .evict("EQUIPMENT_" + exercise.getExerciseEquipment().getId());
    cacheManager.getCache("exercisesByField")
            .evict("TYPE_" + exercise.getExerciseType().getId());
  }

  private void clearExercisesByCategoryCache(Exercise exercise) {
    if (exercise.getExerciseCategoryAssociations() != null) {
      for (ExerciseCategoryAssociation association : exercise.getExerciseCategoryAssociations()) {
        cacheManager.getCache("exercisesByCategory").evict(association.getExerciseCategory().getId());
      }
    }
  }
}

package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Exercise.ExerciseCreateEvent;
import source.code.dto.request.ExerciseCreateDto;

@Component
public class ExerciseCacheListener {
  private final CacheManager cacheManager;

  public ExerciseCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleExerciseCreate(ExerciseCreateEvent event) {
    ExerciseCreateDto dto = event.getExerciseCreateDto();

    cacheManager.getCache("allExercises").clear();

    cacheManager.getCache("exercisesByField").evict("EXPERTISE_LEVEL_" + dto.getExpertiseLevelId());
    cacheManager.getCache("exercisesByField").evict("FORCE_TYPE_" + dto.getForceTypeId());
    cacheManager.getCache("exercisesByField").evict("MECHANICS_TYPE_" + dto.getMechanicsTypeId());
    cacheManager.getCache("exercisesByField").evict("EQUIPMENT_" + dto.getExerciseEquipmentId());
    cacheManager.getCache("exercisesByField").evict("TYPE_" + dto.getExerciseTypeId());


    if (dto.getCategoryIds() != null) {
      for (int categoryId : dto.getCategoryIds()) {
        cacheManager.getCache("exercisesByCategory").evict(categoryId);
      }
    }
  }
}

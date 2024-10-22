package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Exercise.ExerciseInstructionEvent;
import source.code.cache.event.Exercise.ExerciseTipEvent;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.model.Exercise.ExerciseTip;

@Component
public class ExerciseInstructionsTipsListener {
  private final CacheManager cacheManager;

  public ExerciseInstructionsTipsListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleInstruction(ExerciseInstructionEvent event) {
    ExerciseInstruction instruction = event.getInstruction();
    int exerciseId = instruction.getExercise().getId();
    cacheManager.getCache("exerciseInstructions").evict(exerciseId);
  }

  @EventListener
  public void handleTip(ExerciseTipEvent event) {
    ExerciseTip tip = event.getTip();
    int exerciseId = tip.getExercise().getId();
    cacheManager.getCache("exerciseTips").evict(exerciseId);
  }
}

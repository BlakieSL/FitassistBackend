package source.code.service.implementation.Exercise;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.model.Exercise.ExerciseTip;
import source.code.repository.ExerciseInstructionRepository;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.Exercise.ExerciseInstructionsAndTipsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseInstructionsAndTipsServiceImpl implements ExerciseInstructionsAndTipsService {
  private final ExerciseMapper exerciseMapper;
  private final ExerciseInstructionRepository exerciseInstructionRepository;
  private final ExerciseTipRepository exerciseTipRepository;
  public ExerciseInstructionsAndTipsServiceImpl(
          ExerciseMapper exerciseMapper,
          ExerciseInstructionRepository exerciseInstructionRepository,
          ExerciseTipRepository exerciseTipRepository) {
    this.exerciseInstructionRepository = exerciseInstructionRepository;
    this.exerciseTipRepository = exerciseTipRepository;
    this.exerciseMapper = exerciseMapper;
  }

  @Cacheable(value = "exerciseInstructions", key = "#exerciseId")
  public List<ExerciseInstructionResponseDto> getExerciseInstructions(int exerciseId) {
    return exerciseInstructionRepository.getAllByExerciseId(exerciseId).stream()
            .map(exerciseMapper::toInstructionDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "exerciseTips", key = "#exerciseId")
  public List<ExerciseTipResponseDto> getExerciseTips(int exerciseId) {
    return exerciseTipRepository.getAllByExerciseId(exerciseId).stream()
            .map(exerciseMapper::toTipDto)
            .collect(Collectors.toList());
  }
}

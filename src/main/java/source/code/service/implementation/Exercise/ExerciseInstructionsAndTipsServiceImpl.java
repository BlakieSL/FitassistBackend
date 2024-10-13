package source.code.service.implementation.Exercise;

import org.springframework.stereotype.Service;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
import source.code.mapper.ExerciseMapper;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.model.Exercise.ExerciseTip;
import source.code.repository.ExerciseInstructionRepository;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.ExerciseInstructionsAndTipsService;

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

  public List<ExerciseInstructionResponseDto> getExerciseInstructions(int exerciseId) {
    List<ExerciseInstruction> instructions = exerciseInstructionRepository
            .getAllByExerciseId(exerciseId);

    return instructions.stream()
            .map(exerciseMapper::toInstructionDto)
            .collect(Collectors.toList());
  }

  public List<ExerciseTipResponseDto> getExerciseTips(int exerciseId) {
    List<ExerciseTip> tips = exerciseTipRepository.getAllByExerciseId(exerciseId);

    return tips.stream()
            .map(exerciseMapper::toTipDto)
            .collect(Collectors.toList());
  }
}

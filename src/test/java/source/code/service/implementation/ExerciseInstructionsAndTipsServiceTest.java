package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.Text.ExerciseInstructionResponseDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.Exercise;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.model.Exercise.ExerciseTip;
import source.code.repository.ExerciseInstructionRepository;
import source.code.repository.ExerciseTipRepository;
import source.code.service.implementation.Exercise.ExerciseInstructionsAndTipsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExerciseInstructionsAndTipsServiceTest {
  @Mock
  private ExerciseMapper exerciseMapper;
  @Mock
  private ExerciseInstructionRepository exerciseInstructionRepository;
  @Mock
  private ExerciseTipRepository exerciseTipRepository;
  private Exercise exercise1;
  private Exercise exercise2;
  private ExerciseInstruction exerciseInstruction1;
  private ExerciseInstruction exerciseInstruction2;
  private ExerciseTip exerciseTip1;
  private ExerciseTip exerciseTip2;
  private ExerciseInstructionResponseDto instructionResponseDto1;
  private ExerciseInstructionResponseDto instructionResponseDto2;
  private ExerciseTipResponseDto tipResponseDto1;
  private ExerciseTipResponseDto tipResponseDto2;
  @InjectMocks
  private ExerciseInstructionsAndTipsServiceImpl exerciseInstructionsAndTipsService;
  @BeforeEach
  void setup() {
    exercise1 = createExercise(1);
    exercise2 = createExercise(2);

    exerciseInstruction1 = createInstruction(1, exercise1);
    exerciseInstruction2 = createInstruction(2, exercise2);

    exerciseTip1 = createTip(1, exercise1);
    exerciseTip2 = createTip(2, exercise2);

    instructionResponseDto1 = createInstructionResponseDto(exerciseInstruction1.getId());
    instructionResponseDto2 = createInstructionResponseDto(exerciseInstruction2.getId());

    tipResponseDto1 = createTipResponseDto(exerciseTip1.getId());
    tipResponseDto2 = createTipResponseDto(exerciseTip2.getId());
  }

  private Exercise createExercise(int id) {
    return Exercise.createWithId(id);
  }

  private ExerciseInstruction createInstruction(int id, Exercise exercise) {
    return ExerciseInstruction.createWithIdAndExercise(id, exercise);
  }

  private ExerciseTip createTip(int id, Exercise exercise) {
     return ExerciseTip.createWithIdAndExercise(id, exercise);
  }

  private ExerciseInstructionResponseDto createInstructionResponseDto(int id) {
    return ExerciseInstructionResponseDto.createWithId(id);
  }

  private ExerciseTipResponseDto createTipResponseDto(int id) {
    return ExerciseTipResponseDto.createWithId(id);
  }
  /*

  @Test
  void getExerciseInstructions_shouldReturnInstructions_whenInstructionsFound() {
    // Arrange
    int exerciseId = exercise1.getId();
    List<ExerciseInstruction> listOfInstructions = List.of(exerciseInstruction1);
    List<ExerciseInstructionResponseDto> listOfInstructionsResponseDto = List.of(instructionResponseDto1);

    when(exerciseInstructionRepository.getAllByExerciseId(exercise1.getId())).thenReturn(listOfInstructions);
    when(exerciseMapper.toInstructionDto(exerciseInstruction1)).thenReturn(instructionResponseDto1);

    // Act
    List<ExerciseInstructionResponseDto> result = exerciseInstructionsAndTipsService
            .getInstructions(exerciseId);

    // Assert
    verify(exerciseInstructionRepository, times(1)).getAllByExerciseId(exerciseId);
    verify(exerciseMapper, times(1)).toInstructionDto(exerciseInstruction1);
    assertEquals(listOfInstructionsResponseDto, result);
    assertEquals(listOfInstructionsResponseDto.get(0).getId(), result.get(0).getId());
  }

  @Test
  void getExerciseInstructions_shouldReturnEmptyList_whenNoInstructionsFound() {
    int exerciseId = exercise1.getId();
    List<ExerciseInstruction> emptyList = List.of();

    when(exerciseInstructionRepository.getAllByExerciseId(exerciseId)).thenReturn(emptyList);

    // Act
    List<ExerciseInstructionResponseDto> result = exerciseInstructionsAndTipsService
            .getInstructions(exerciseId);

    // Assert
    verify(exerciseInstructionRepository, times(1)).getAllByExerciseId(exerciseId);
    verify(exerciseMapper, never()).toInstructionDto(any());
    assertEquals(0, result.size());
  }

  @Test
  void getExerciseTips_shouldReturnTips_whenTipsFound() {
    // Arrange
    int exerciseId = exercise1.getId();
    List<ExerciseTip> listOfTips = List.of(exerciseTip1);
    List<ExerciseTipResponseDto> listOfTipsResponseDto = List.of(tipResponseDto1);

    when(exerciseTipRepository.getAllByExerciseId(exerciseId)).thenReturn(listOfTips);
    when(exerciseMapper.toTipDto(exerciseTip1)).thenReturn(tipResponseDto1);

    // Act
    List<ExerciseTipResponseDto> result = exerciseInstructionsAndTipsService
            .getTips(exerciseId);

    // Assert
    verify(exerciseTipRepository, times(1)).getAllByExerciseId(exerciseId);
    verify(exerciseMapper, times(1)).toTipDto(exerciseTip1);
    assertEquals(listOfTipsResponseDto, result);
    assertEquals(listOfTipsResponseDto.get(0).getId(), result.get(0).getId());
  }

  @Test
  void getExerciseTips_shouldReturnEmptyList_whenNoTipsFound() {
    // Arrange
    int exerciseId = exercise1.getId();
    List<ExerciseTip> emptyList = List.of();

    when(exerciseTipRepository.getAllByExerciseId(exerciseId)).thenReturn(emptyList);

    // Act
    List<ExerciseTipResponseDto> result = exerciseInstructionsAndTipsService
            .getTips(exerciseId);

    // Assert
    verify(exerciseTipRepository, times(1)).getAllByExerciseId(exerciseId);
    verify(exerciseMapper, never()).toTipDto(any());
    assertEquals(0, result.size());
  }
   */
}

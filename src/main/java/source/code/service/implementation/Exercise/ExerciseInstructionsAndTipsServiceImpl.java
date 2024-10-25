package source.code.service.implementation.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.cache.event.Exercise.ExerciseInstructionEvent;
import source.code.cache.event.Exercise.ExerciseTipEvent;
import source.code.dto.request.Text.ExerciseInstructionUpdateDto;
import source.code.dto.request.Text.ExerciseTipUpdateDto;
import source.code.dto.response.Text.ExerciseInstructionResponseDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.ExerciseInstruction;
import source.code.model.Text.ExerciseTip;
import source.code.repository.ExerciseInstructionRepository;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.Exercise.ExerciseInstructionsAndTipsService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Helpers.ValidationService;

import java.util.List;

@Service
public class ExerciseInstructionsAndTipsServiceImpl implements ExerciseInstructionsAndTipsService {
  private final ValidationService validationService;
  private final JsonPatchService jsonPatchService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TextMapper instructionsTipsMapper;
  private final RepositoryHelper repositoryHelper;
  private final ExerciseInstructionRepository exerciseInstructionRepository;
  private final ExerciseTipRepository exerciseTipRepository;
  public ExerciseInstructionsAndTipsServiceImpl(
          ValidationService validationService,
          JsonPatchService jsonPatchService,
          ApplicationEventPublisher applicationEventPublisher,
          TextMapper instructionsTipsMapper,
          RepositoryHelper repositoryHelper,
          ExerciseInstructionRepository exerciseInstructionRepository,
          ExerciseTipRepository exerciseTipRepository) {
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.repositoryHelper = repositoryHelper;
    this.exerciseInstructionRepository = exerciseInstructionRepository;
    this.exerciseTipRepository = exerciseTipRepository;
    this.instructionsTipsMapper = instructionsTipsMapper;
  }

  @Override
  @Transactional
  public void deleteInstruction(int instructionId) {
    ExerciseInstruction instruction = findInstruction(instructionId);
    exerciseInstructionRepository.delete(instruction);

    applicationEventPublisher.publishEvent(new ExerciseInstructionEvent(this, instruction));
  }

  @Override
  @Transactional
  public void deleteTip(int tipId) {
    ExerciseTip tip = findTip(tipId);
    exerciseTipRepository.delete(tip);

    applicationEventPublisher.publishEvent(new ExerciseTipEvent(this, tip));
  }

  @Override
  @Transactional
  public void updateInstruction(int instructionId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseInstruction instruction = findInstruction(instructionId);
    ExerciseInstructionUpdateDto patched = applyPatchToInstruction(instruction, patch);

    validationService.validate(patched);

    instructionsTipsMapper.updateExerciseInstruction(instruction, patched);
    ExerciseInstruction saved = exerciseInstructionRepository.save(instruction);

    applicationEventPublisher.publishEvent(new ExerciseInstructionEvent(this, saved));
  }

  @Override
  @Transactional
  public void updateTip(int tipId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseTip tip = findTip(tipId);
    ExerciseTipUpdateDto patched = applyPatchToTip(tip, patch);

    validationService.validate(patched);

    instructionsTipsMapper.updateExerciseTip(tip, patched);
    ExerciseTip saved = exerciseTipRepository.save(tip);

    applicationEventPublisher.publishEvent(new ExerciseTipEvent(this, saved));
  }

  @Override
  @Cacheable(value = "exerciseInstructions", key = "#exerciseId")
  public List<ExerciseInstructionResponseDto> getInstructions(int exerciseId) {
    return exerciseInstructionRepository.getAllByExerciseId(exerciseId).stream()
            .map(instructionsTipsMapper::toExerciseInstructionResponseDto)
            .toList();
  }

  @Override
  @Cacheable(value = "exerciseTips", key = "#exerciseId")
  public List<ExerciseTipResponseDto> getTips(int exerciseId) {
    return exerciseTipRepository.getAllByExerciseId(exerciseId).stream()
            .map(instructionsTipsMapper::toExerciseTipResponseDto)
            .toList();
  }

  private ExerciseInstruction findInstruction(int instructionId) {
    return repositoryHelper.find(exerciseInstructionRepository, ExerciseInstruction.class, instructionId);
  }

  private ExerciseTip findTip(int tipId) {
    return repositoryHelper.find(exerciseTipRepository, ExerciseTip.class, tipId);
  }

  private ExerciseInstructionUpdateDto applyPatchToInstruction(ExerciseInstruction instruction,
                                                               JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    ExerciseInstructionResponseDto responseDto = instructionsTipsMapper
            .toExerciseInstructionResponseDto(instruction);
    return jsonPatchService.applyPatch(patch, responseDto, ExerciseInstructionUpdateDto.class);
  }

  private ExerciseTipUpdateDto applyPatchToTip(ExerciseTip tip, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    ExerciseTipResponseDto responseDto = instructionsTipsMapper.toExerciseTipResponseDto(tip);
    return jsonPatchService.applyPatch(patch, responseDto, ExerciseTipUpdateDto.class);
  }
}

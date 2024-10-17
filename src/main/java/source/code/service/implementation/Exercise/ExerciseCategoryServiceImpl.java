package source.code.service.implementation.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.ExerciseCategoryCreateDto;
import source.code.dto.request.ExerciseCategoryUpdateDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Exercise.ExerciseCategoryMapper;
import source.code.model.Exercise.ExerciseCategory;
import source.code.repository.ExerciseCategoryRepository;
import source.code.service.declaration.ExerciseCategoryService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@Service
public class ExerciseCategoryServiceImpl implements ExerciseCategoryService {
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ExerciseCategoryRepository exerciseCategoryRepository;
  private final ExerciseCategoryMapper exerciseCategoryMapper;

  public ExerciseCategoryServiceImpl(ValidationHelper validationHelper,
                                     JsonPatchHelper jsonPatchHelper,
                                     ApplicationEventPublisher applicationEventPublisher,
                                     ExerciseCategoryRepository exerciseCategoryRepository,
                                     ExerciseCategoryMapper exerciseCategoryMapper) {
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
    this.applicationEventPublisher = applicationEventPublisher;
    this.exerciseCategoryRepository = exerciseCategoryRepository;
    this.exerciseCategoryMapper = exerciseCategoryMapper;
  }

  @Transactional
  public ExerciseCategoryResponseDto createExerciseCategory(ExerciseCategoryCreateDto request) {
    ExerciseCategory category = exerciseCategoryRepository
            .save(exerciseCategoryMapper.toEntity(request));

    return exerciseCategoryMapper.toResponseDto(category);
  }


  @Transactional
  public void updateExercise(int exerciseCategoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseCategory category = getCategory(exerciseCategoryId);
    ExerciseCategoryUpdateDto patchedCategory = applyPatchToCategory(category, patch);

    validationHelper.validate(patchedCategory);

    exerciseCategoryMapper.updateExercise(category, patchedCategory);
    ExerciseCategory savedCategory = exerciseCategoryRepository.save(category);
  }

  @Transactional
  public void deleteExercise(int exerciseCategoryId) {
    ExerciseCategory category = getCategory(exerciseCategoryId);
    exerciseCategoryRepository.delete(category);
  }

  @Cacheable(value = "allExerciseCategories")
  public List<ExerciseCategoryResponseDto> getAllCategories() {
    List<ExerciseCategory> categories = exerciseCategoryRepository.findAll();

    return categories.stream()
            .map(exerciseCategoryMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public ExerciseCategoryResponseDto getExerciseCategory(int exerciseCategoryId) {
    ExerciseCategory category = getCategory(exerciseCategoryId);
    return exerciseCategoryMapper.toResponseDto(category);
  }

  private ExerciseCategory getCategory(int categoryId) {
    return exerciseCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(
                    "ExerciseCategory with id: " + categoryId + " not found"));
  }

  private ExerciseCategoryUpdateDto applyPatchToCategory(ExerciseCategory category, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ExerciseCategoryResponseDto responseDto = exerciseCategoryMapper.toResponseDto(category);
    return jsonPatchHelper.applyPatch(patch, responseDto, ExerciseCategoryUpdateDto.class);
  }
}

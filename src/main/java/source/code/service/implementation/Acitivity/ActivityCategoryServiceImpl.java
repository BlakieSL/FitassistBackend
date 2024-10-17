package source.code.service.implementation.Acitivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Activity.ActivityCategoryCreateEvent;
import source.code.cache.event.Activity.ActivityCategoryDeleteEvent;
import source.code.cache.event.Activity.ActivityCategoryUpdateEvent;
import source.code.dto.request.ActivityCategoryCreateDto;
import source.code.dto.request.ActivityCategoryUpdateDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Activity.ActivityCategoryMapper;
import source.code.model.Activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.ActivityCategoryService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ActivityCategoryServiceImpl implements ActivityCategoryService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final ActivityCategoryRepository activityCategoryRepository;
  private final ActivityCategoryMapper activityCategoryMapper;
  public ActivityCategoryServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                     ValidationHelper validationHelper,
                                     JsonPatchHelper jsonPatchHelper,
                                     ActivityCategoryRepository activityCategoryRepository,
                                     ActivityCategoryMapper activityCategoryMapper) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
    this.activityCategoryRepository = activityCategoryRepository;
    this.activityCategoryMapper = activityCategoryMapper;
  }

  @Transactional
  public ActivityCategoryResponseDto createCategory(ActivityCategoryCreateDto request) {
    ActivityCategory activityCategory = activityCategoryRepository
            .save(activityCategoryMapper.toEntity(request));

    applicationEventPublisher.publishEvent(new ActivityCategoryCreateEvent(this));

    return activityCategoryMapper.toResponseDto(activityCategory);
  }

  @Transactional
  public void updateCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ActivityCategory category = getCategory(categoryId);
    ActivityCategoryUpdateDto patchedCategory = applyPatchToCategory(category, patch);

    validationHelper.validate(patchedCategory);

    activityCategoryMapper.updateCategory(category, patchedCategory);
    ActivityCategory savedCategory = activityCategoryRepository.save(category);

    applicationEventPublisher.publishEvent(new ActivityCategoryUpdateEvent(this));
  }

  @Transactional
  public void deleteCategory(int categoryId) {
    ActivityCategory activityCategory = getCategory(categoryId);
    activityCategoryRepository.delete(activityCategory);

    applicationEventPublisher.publishEvent(new ActivityCategoryDeleteEvent(this));
  }

  @Cacheable(value = "allActivityCategories")
  public List<ActivityCategoryResponseDto> getAllCategories() {
    List<ActivityCategory> categories = activityCategoryRepository.findAll();

    return categories.stream()
            .map(activityCategoryMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public ActivityCategoryResponseDto getById(int categoryId) {
    ActivityCategory category = getCategory(categoryId);
    return activityCategoryMapper.toResponseDto(category);
  }

  private ActivityCategory getCategory(int categoryId) {
    return activityCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(
                    "ActivityCategory with id: " + categoryId + " not found"));
  }

  private ActivityCategoryUpdateDto applyPatchToCategory(ActivityCategory category, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    ActivityCategoryResponseDto responseDto = activityCategoryMapper.toResponseDto(category);
    return jsonPatchHelper.applyPatch(patch, responseDto, ActivityCategoryUpdateDto.class);
  }
}

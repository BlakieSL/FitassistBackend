package source.code.service.implementation.Plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.cache.event.Plan.PlanCreateEvent;
import source.code.cache.event.Plan.PlanDeleteEvent;
import source.code.cache.event.Plan.PlanUpdateEvent;
import source.code.dto.request.Plan.PlanCreateDto;
import source.code.dto.request.Plan.PlanUpdateDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.helper.enumerators.PlanField;
import source.code.mapper.Plan.PlanMapper;
import source.code.model.Plan.*;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.service.declaration.Plan.PlanService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlanServiceImpl implements PlanService {
  private final PlanMapper planMapper;
  private final JsonPatchServiceImpl jsonPatchServiceImpl;
  private final ValidationServiceImpl validationServiceImpl;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PlanRepository planRepository;
  private final UserPlanRepository userPlanRepository;
  private final PlanCategoryRepository planCategoryRepository;
  private final PlanCategoryAssociationRepository planCategoryAssociationRepository;

  public PlanServiceImpl(PlanMapper planMapper,
                         JsonPatchServiceImpl jsonPatchServiceImpl,
                         ValidationServiceImpl validationServiceImpl,
                         ApplicationEventPublisher applicationEventPublisher,
                         PlanRepository planRepository,
                         UserPlanRepository userPlanRepository,
                         PlanCategoryRepository planCategoryRepository,
                         PlanCategoryAssociationRepository planCategoryAssociationRepository) {
    this.planMapper = planMapper;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
    this.validationServiceImpl = validationServiceImpl;
    this.applicationEventPublisher = applicationEventPublisher;
    this.planRepository = planRepository;
    this.userPlanRepository = userPlanRepository;
    this.planCategoryRepository = planCategoryRepository;
    this.planCategoryAssociationRepository = planCategoryAssociationRepository;
  }

  @Transactional
  public PlanResponseDto createPlan(PlanCreateDto request) {
    Plan plan = planRepository.save(planMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new PlanCreateEvent(this, plan));

    return planMapper.toResponseDto(plan);
  }

  @Transactional
  public void updatePlan(int planId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    Plan plan = getPlanOrThrow(planId);
    PlanUpdateDto patchedPlanUpdateDto = applyPatchToPlan(plan, patch);

    validationServiceImpl.validate(patchedPlanUpdateDto);

    planMapper.updatePlan(plan, patchedPlanUpdateDto);
    Plan savedPlan = planRepository.save(plan);

    applicationEventPublisher.publishEvent(new PlanUpdateEvent(this, savedPlan));
  }

  @Transactional
  public void deletePlan(int planId) {
    Plan plan = getPlanOrThrow(planId);
    planRepository.delete(plan);

    applicationEventPublisher.publishEvent(new PlanDeleteEvent(this, plan));
  }

  @Cacheable(value = {"plans"}, key = "#id")
  public PlanResponseDto getPlan(int id) {
    Plan plan = getPlanOrThrow(id);
    return planMapper.toResponseDto(plan);
  }

  @Cacheable(value = {"allPlans"})
  public List<PlanResponseDto> getAllPlans() {
    List<Plan> plans = planRepository.findAll();

    return plans.stream()
            .map(planMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"plansByCategory"}, key = "#categoryId")
  public List<PlanResponseDto> getPlansByCategory(int categoryId) {
    List<PlanCategoryAssociation> planCategoryAssociations =
            planCategoryAssociationRepository.findByPlanCategoryId(categoryId);
    List<Plan> plans = planCategoryAssociations.stream()
            .map(PlanCategoryAssociation::getPlan)
            .collect(Collectors.toList());

    return plans.stream()
            .map(planMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = "plansByField", key = "#field.name() + '_' + #value")
  public List<PlanResponseDto> getPlansByField(PlanField field, int value) {
    switch (field) {
      case TYPE:
        return getPlansByField(Plan::getPlanType, PlanType::getId, value);
      case DURATION:
        return getPlansByField(Plan::getPlanDuration, PlanDuration::getId, value);
      case EQUIPMENT:
        return getPlansByField(Plan::getPlanEquipment, PlanEquipment::getId, value);
      case EXPERTISE_LEVEL:
        return getPlansByField(Plan::getPlanExpertiseLevel, PlanExpertiseLevel::getId, value);
      default:
        throw new IllegalArgumentException("Unknown field: " + field);
    }
  }

  private <T> List<PlanResponseDto> getPlansByField(Function<Plan, T> fieldExtractor,
                                                    Function<T, Integer> idExtractor,
                                                    int fieldValue) {
    List<Plan> plans = planRepository.findAll().stream()
            .filter(plan -> idExtractor.apply(fieldExtractor.apply(plan)).equals(fieldValue))
            .collect(Collectors.toList());

    return plans.stream()
            .map(planMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  private Plan getPlanOrThrow(int planId) {
    return planRepository.findById(planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + planId + " not found"));
  }

  private PlanUpdateDto applyPatchToPlan (Plan plan, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    PlanResponseDto responseDto = planMapper.toResponseDto(plan);
    return jsonPatchServiceImpl.applyPatch(patch, responseDto, PlanUpdateDto.class);
  }
}
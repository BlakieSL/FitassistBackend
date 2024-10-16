package source.code.service.implementation.Plan;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.helper.enumerators.PlanField;
import source.code.mapper.PlanMapper;
import source.code.model.Plan.*;
import source.code.model.User.UserPlan;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.service.declaration.PlanService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlanServiceImpl implements PlanService {
  private final PlanMapper planMapper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PlanRepository planRepository;
  private final UserPlanRepository userPlanRepository;
  private final PlanCategoryRepository planCategoryRepository;
  private final PlanCategoryAssociationRepository planCategoryAssociationRepository;

  public PlanServiceImpl(PlanMapper planMapper,
                         ApplicationEventPublisher applicationEventPublisher,
                         PlanRepository planRepository,
                         UserPlanRepository userPlanRepository,
                         PlanCategoryRepository planCategoryRepository,
                         PlanCategoryAssociationRepository planCategoryAssociationRepository) {
    this.planMapper = planMapper;
    this.applicationEventPublisher = applicationEventPublisher;
    this.planRepository = planRepository;
    this.userPlanRepository = userPlanRepository;
    this.planCategoryRepository = planCategoryRepository;
    this.planCategoryAssociationRepository = planCategoryAssociationRepository;
  }

  @Transactional
  public PlanResponseDto createPlan(PlanCreateDto request) {
    Plan plan = planRepository.save(planMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new source.code.cache.event.Plan.Plan.PlanCreateEvent(this, request));

    return planMapper.toDto(plan);
  }

  @Cacheable(value = {"plans"}, key = "#id")
  public PlanResponseDto getPlan(int id) {
    Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + id + " not found"));

    return planMapper.toDto(plan);
  }

  @Cacheable(value = {"allPlans"})
  public List<PlanResponseDto> getAllPlans() {
    List<Plan> plans = planRepository.findAll();

    return plans.stream()
            .map(planMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<PlanResponseDto> getPlansByUserAndType(int userId, short type) {
    List<UserPlan> userPlans = userPlanRepository.findByUserIdAndType(userId, type);
    List<Plan> plans = userPlans.stream()
            .map(UserPlan::getPlan)
            .collect(Collectors.toList());

    return plans.stream()
            .map(planMapper::toDto)
            .collect(Collectors.toList());
  }

  @Cacheable(value = {"allPlanCategories"})
  public List<PlanCategoryResponseDto> getAllCategories() {
    List<PlanCategory> categories = planCategoryRepository.findAll();

    return categories.stream()
            .map(planMapper::toCategoryDto)
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
            .map(planMapper::toDto)
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
            .map(planMapper::toDto)
            .collect(Collectors.toList());
  }
}
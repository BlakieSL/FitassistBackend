package source.code.service.Implementation.Plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Request.Plan.PlanCreateDto;
import source.code.dto.Request.Plan.PlanUpdateDto;
import source.code.dto.Response.Category.EquipmentResponseDto;
import source.code.dto.Response.PlanResponseDto;
import source.code.event.events.Plan.PlanCreateEvent;
import source.code.event.events.Plan.PlanDeleteEvent;
import source.code.event.events.Plan.PlanUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.mapper.Plan.PlanMapper;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Plan.PlanService;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.PlanSpecification;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
  private final JsonPatchService jsonPatchService;
  private final ValidationService validationService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PlanMapper planMapper;
  private final RepositoryHelper repositoryHelper;
  private final PlanRepository planRepository;
  private final PlanCategoryAssociationRepository planCategoryAssociationRepository;

  public PlanServiceImpl(PlanMapper planMapper,
                         JsonPatchService jsonPatchService,
                         ValidationService validationService,
                         ApplicationEventPublisher applicationEventPublisher,
                         RepositoryHelper repositoryHelper,
                         PlanRepository planRepository,
                         PlanCategoryAssociationRepository planCategoryAssociationRepository) {
    this.planMapper = planMapper;
    this.jsonPatchService = jsonPatchService;
    this.validationService = validationService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.repositoryHelper = repositoryHelper;
    this.planRepository = planRepository;
    this.planCategoryAssociationRepository = planCategoryAssociationRepository;
  }

  @Override
  @Transactional
  public PlanResponseDto createPlan(PlanCreateDto request) {
    Plan plan = planRepository.save(planMapper.toEntity(request));
    applicationEventPublisher.publishEvent(new PlanCreateEvent(this, plan));

    return planMapper.toResponseDto(plan);
  }

  @Override
  @Transactional
  public void updatePlan(int planId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    Plan plan = find(planId);
    PlanUpdateDto patchedPlanUpdateDto = applyPatchToPlan(plan, patch);

    validationService.validate(patchedPlanUpdateDto);

    planMapper.updatePlan(plan, patchedPlanUpdateDto);
    Plan savedPlan = planRepository.save(plan);

    applicationEventPublisher.publishEvent(new PlanUpdateEvent(this, savedPlan));
  }

  @Override
  @Transactional
  public void deletePlan(int planId) {
    Plan plan = find(planId);
    planRepository.delete(plan);

    applicationEventPublisher.publishEvent(new PlanDeleteEvent(this, plan));
  }

  @Override
  @Cacheable(value = CacheNames.PLANS, key = "#id")
  public PlanResponseDto getPlan(int id) {
    Plan plan = find(id);
    return planMapper.toResponseDto(plan);
  }

  @Override
  @Cacheable(value = CacheNames.ALL_PLANS)
  public List<PlanResponseDto> getAllPlans() {
    return repositoryHelper.findAll(planRepository, planMapper::toResponseDto);
  }

  @Override
  public List<PlanResponseDto> getFilteredPlans(FilterDto filter) {
    SpecificationFactory<Plan> planFactory = PlanSpecification::new;
    SpecificationBuilder<Plan> specificationBuilder = SpecificationBuilder.create(filter, planFactory);
    Specification<Plan> specification = specificationBuilder.build();

    return planRepository.findAll(specification).stream()
            .map(planMapper::toResponseDto)
            .toList();
  }

  @Override
  @Cacheable(value = CacheNames.PLANS_BY_CATEGORY, key = "#categoryId")
  public List<PlanResponseDto> getPlansByCategory(int categoryId) {
    return planCategoryAssociationRepository.findByPlanCategoryId(categoryId).stream()
            .map(PlanCategoryAssociation::getPlan)
            .map(planMapper::toResponseDto)
            .toList();
  }

  @Override
  public List<EquipmentResponseDto> getAllEquipment(int planId) {
    return planRepository.findAllEquipmentByPlanId(planId);
  }

  @Override
  public List<Plan> getAllPlanEntities() {
    return planRepository.findAllWithoutAssociations();
  }


  private Plan find(int planId) {
    return repositoryHelper.find(planRepository, Plan.class, planId);
  }

  private PlanUpdateDto applyPatchToPlan(Plan plan, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    PlanResponseDto responseDto = planMapper.toResponseDto(plan);
    return jsonPatchService.applyPatch(patch, responseDto, PlanUpdateDto.class);
  }
}
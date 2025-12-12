package source.code.service.implementation.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.request.plan.PlanUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.plan.PlanCategoriesResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.event.events.Plan.PlanCreateEvent;
import source.code.event.events.Plan.PlanDeleteEvent;
import source.code.event.events.Plan.PlanUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.Enum.model.PlanStructureType;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.repository.EquipmentRepository;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.PlanRepository;
import source.code.repository.TextRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.declaration.plan.PlanService;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
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
    private final TextRepository textRepository;
    private final SpecificationDependencies dependencies;
    private final PlanPopulationService planPopulationService;
    private final PlanCategoryRepository planCategoryRepository;
    private final EquipmentRepository equipmentRepository;

    public PlanServiceImpl(PlanMapper planMapper,
                           JsonPatchService jsonPatchService,
                           ValidationService validationService,
                           ApplicationEventPublisher applicationEventPublisher,
                           RepositoryHelper repositoryHelper,
                           PlanRepository planRepository,
                           TextRepository textRepository,
                           SpecificationDependencies dependencies,
                           PlanPopulationService planPopulationService,
                           PlanCategoryRepository planCategoryRepository,
                           EquipmentRepository equipmentRepository) {
        this.planMapper = planMapper;
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.repositoryHelper = repositoryHelper;
        this.planRepository = planRepository;
        this.textRepository = textRepository;
        this.dependencies = dependencies;
        this.planPopulationService = planPopulationService;
        this.planCategoryRepository = planCategoryRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    @Transactional
    public PlanResponseDto createPlan(PlanCreateDto request) {
        int userId = AuthorizationUtil.getUserId();
        Plan mapped = planMapper.toEntity(request, userId);
        Plan saved = planRepository.save(mapped);
        applicationEventPublisher.publishEvent(PlanCreateEvent.of(this, saved));

        planRepository.flush();

        return findAndMap(saved.getId());
    }

    @Override
    @Transactional
    public void updatePlan(int planId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        Plan plan = find(planId);
        PlanUpdateDto patchedPlanUpdateDto = applyPatchToPlan(patch);

        validationService.validate(patchedPlanUpdateDto);
        planMapper.updatePlan(plan, patchedPlanUpdateDto);
        Plan savedPlan = planRepository.save(plan);

        applicationEventPublisher.publishEvent(PlanUpdateEvent.of(this, savedPlan));
    }

    @Override
    @Transactional
    public void deletePlan(int planId) {
        Plan plan = find(planId);

        textRepository.deleteByPlanId(planId);
        planRepository.delete(plan);

        applicationEventPublisher.publishEvent(PlanDeleteEvent.of(this, plan));
    }

    @Override
    @Cacheable(value = CacheNames.PLANS, key = "#id")
    public PlanResponseDto getPlan(int id) {
        return findAndMap(id);
    }

    @Override
    public Page<PlanSummaryDto> getFilteredPlans(FilterDto filter, Pageable pageable) {
        SpecificationFactory<Plan> planFactory = PlanSpecification::new;
        SpecificationBuilder<Plan> specificationBuilder = SpecificationBuilder.of(filter, planFactory, dependencies);
        Specification<Plan> specification = specificationBuilder.build();

        Page<Plan> planPage = planRepository.findAll(specification, pageable);

        List<PlanSummaryDto> summaries = planPage.getContent().stream()
                .map(planMapper::toSummaryDto)
                .toList();

        planPopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, planPage.getTotalElements());
    }

    @Override
    public List<Plan> getAllPlanEntities() {
        return planRepository.findAllWithoutAssociations();
    }

    @Override
    @Transactional
    public void incrementViews(int planId) {
        planRepository.incrementViews(planId);
    }

    @Override
    @Cacheable(value = CacheNames.PLAN_CATEGORIES)
    public PlanCategoriesResponseDto getAllPlanCategories() {
        var structureTypes = List.of(PlanStructureType.values());

        var categories = planCategoryRepository.findAll().stream()
                .map(category -> new CategoryResponseDto(category.getId(), category.getName()))
                .toList();

        var equipments = equipmentRepository.findAll().stream()
                .map(equipment -> new CategoryResponseDto(equipment.getId(), equipment.getName()))
                .toList();

        return new PlanCategoriesResponseDto(structureTypes, categories, equipments);
    }

    private Plan find(int planId) {
        return repositoryHelper.find(planRepository, Plan.class, planId);
    }

    private PlanResponseDto findAndMap(int planId) {
        Plan plan = planRepository.findByIdWithDetails(planId)
                .orElseThrow(() -> RecordNotFoundException.of(Plan.class, planId));
        PlanResponseDto dto = planMapper.toResponseDto(plan);
        planPopulationService.populate(dto);

        return dto;
    }

    private PlanUpdateDto applyPatchToPlan(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return jsonPatchService.createFromPatch(patch, PlanUpdateDto.class);
    }
}
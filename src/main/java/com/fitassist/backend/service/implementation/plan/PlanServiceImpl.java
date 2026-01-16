package com.fitassist.backend.service.implementation.plan;

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
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.plan.PlanCategoriesResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.event.events.Plan.PlanCreateEvent;
import com.fitassist.backend.event.events.Plan.PlanDeleteEvent;
import com.fitassist.backend.event.events.Plan.PlanUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.model.plan.PlanStructureType;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.repository.EquipmentRepository;
import com.fitassist.backend.repository.PlanCategoryRepository;
import com.fitassist.backend.repository.PlanRepository;
import com.fitassist.backend.repository.TextRepository;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.declaration.plan.PlanService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.PlanSpecification;

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

	public PlanServiceImpl(PlanMapper planMapper, JsonPatchService jsonPatchService,
			ValidationService validationService, ApplicationEventPublisher applicationEventPublisher,
			RepositoryHelper repositoryHelper, PlanRepository planRepository, TextRepository textRepository,
			SpecificationDependencies dependencies, PlanPopulationService planPopulationService,
			PlanCategoryRepository planCategoryRepository, EquipmentRepository equipmentRepository) {
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
	public void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
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

		List<PlanSummaryDto> summaries = planPage.getContent().stream().map(planMapper::toSummaryDto).toList();

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
	public PlanCategoriesResponseDto getAllPlanCategories() {
		var structureTypes = List.of(PlanStructureType.values());

		var categories = planCategoryRepository.findAll()
			.stream()
			.map(category -> new CategoryResponseDto(category.getId(), category.getName()))
			.toList();

		var equipments = equipmentRepository.findAll()
			.stream()
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

	private PlanUpdateDto applyPatchToPlan(JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, PlanUpdateDto.class);
	}

}

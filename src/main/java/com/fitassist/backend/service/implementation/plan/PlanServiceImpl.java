package com.fitassist.backend.service.implementation.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.request.plan.PlanUpdateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.plan.PlanCategoriesResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.event.event.Plan.PlanCreateEvent;
import com.fitassist.backend.event.event.Plan.PlanDeleteEvent;
import com.fitassist.backend.event.event.Plan.PlanUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.mapper.plan.PlanMappingContext;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.model.plan.PlanStructureType;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.*;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.declaration.plan.PlanService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.PlanSpecification;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	private final ExerciseRepository exerciseRepository;

	private final UserRepository userRepository;

	public PlanServiceImpl(PlanMapper planMapper, JsonPatchService jsonPatchService,
			ValidationService validationService, ApplicationEventPublisher applicationEventPublisher,
			RepositoryHelper repositoryHelper, PlanRepository planRepository, TextRepository textRepository,
			SpecificationDependencies dependencies, PlanPopulationService planPopulationService,
			PlanCategoryRepository planCategoryRepository, EquipmentRepository equipmentRepository,
			ExerciseRepository exerciseRepository, UserRepository userRepository) {
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
		this.exerciseRepository = exerciseRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public PlanResponseDto createPlan(PlanCreateDto request) {
		PlanMappingContext context = prepareCreateContext(request);
		Plan mapped = planMapper.toEntity(request, context);
		Plan saved = planRepository.save(mapped);

		applicationEventPublisher.publishEvent(PlanCreateEvent.of(this, saved));
		planRepository.flush();

		return findAndMap(saved.getId());
	}

	private PlanResponseDto findAndMap(int planId) {
		Plan plan = planRepository.findByIdWithDetails(planId)
			.orElseThrow(() -> RecordNotFoundException.of(Plan.class, planId));

		PlanResponseDto dto = planMapper.toResponse(plan);
		planPopulationService.populate(dto);

		return dto;
	}

	private PlanMappingContext prepareCreateContext(PlanCreateDto request) {
		int userId = AuthorizationUtil.getUserId();
		User user = userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));

		List<PlanCategory> categories = findCategories(request.getCategoryIds());
		List<Exercise> exercises = exerciseRepository.findAllById(extractExerciseIds(request));

		return PlanMappingContext.forCreate(user, categories, exercises);
	}

	private List<PlanCategory> findCategories(List<Integer> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			return List.of();
		}
		return planCategoryRepository.findAllByIdIn(categoryIds);
	}

	private Set<Integer> extractExerciseIds(PlanCreateDto request) {
		if (request.getWorkouts() == null) {
			return Set.of();
		}
		return request.getWorkouts()
			.stream()
			.flatMap(w -> Stream.ofNullable(w.getWorkoutSets()).flatMap(Collection::stream))
			.flatMap(ws -> Stream.ofNullable(ws.getWorkoutSetExercises()).flatMap(Collection::stream))
			.map(WorkoutSetExerciseNestedCreateDto::getExerciseId)
			.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Plan plan = find(planId);
		PlanUpdateDto patchedPlanUpdateDto = jsonPatchService.createFromPatch(patch, PlanUpdateDto.class);
		validationService.validate(patchedPlanUpdateDto);

		PlanMappingContext context = prepareUpdateContext(patchedPlanUpdateDto);
		planMapper.update(plan, patchedPlanUpdateDto, context);

		Plan savedPlan = planRepository.save(plan);
		applicationEventPublisher.publishEvent(PlanUpdateEvent.of(this, savedPlan));
	}

	private Plan find(int planId) {
		return repositoryHelper.find(planRepository, Plan.class, planId);
	}

	private PlanMappingContext prepareUpdateContext(PlanUpdateDto dto) {
		List<PlanCategory> categories = findCategories(dto.getCategoryIds());
		List<Exercise> exercises = exerciseRepository.findAllById(extractExerciseIds(dto));

		return PlanMappingContext.forUpdate(categories, exercises);
	}

	private Set<Integer> extractExerciseIds(PlanUpdateDto request) {
		if (request.getWorkouts() == null) {
			return Set.of();
		}
		return request.getWorkouts()
			.stream()
			.flatMap(w -> Stream.ofNullable(w.getWorkoutSets()).flatMap(Collection::stream))
			.flatMap(ws -> Stream.ofNullable(ws.getWorkoutSetExercises()).flatMap(Collection::stream))
			.map(WorkoutSetExerciseNestedUpdateDto::getExerciseId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
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
		List<PlanSummaryDto> summaries = planPage.getContent().stream().map(planMapper::toSummary).toList();

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
		List<PlanStructureType> structureTypes = List.of(PlanStructureType.values());

		List<CategoryResponseDto> categories = planCategoryRepository.findAll()
			.stream()
			.map(category -> new CategoryResponseDto(category.getId(), category.getName()))
			.toList();

		List<CategoryResponseDto> equipments = equipmentRepository.findAll()
			.stream()
			.map(equipment -> new CategoryResponseDto(equipment.getId(), equipment.getName()))
			.toList();

		return new PlanCategoriesResponseDto(structureTypes, categories, equipments);
	}

}

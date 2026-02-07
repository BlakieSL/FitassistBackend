package com.fitassist.backend.service.implementation.exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.exercise.ExerciseCreateDto;
import com.fitassist.backend.dto.request.exercise.ExerciseUpdateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseCategoriesResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseResponseDto;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.event.event.Exercise.ExerciseCreateEvent;
import com.fitassist.backend.event.event.Exercise.ExerciseDeleteEvent;
import com.fitassist.backend.event.event.Exercise.ExerciseUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.exercise.ExerciseMapper;
import com.fitassist.backend.mapper.exercise.ExerciseMappingContext;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.exercise.*;
import com.fitassist.backend.repository.*;
import com.fitassist.backend.service.declaration.exercise.ExercisePopulationService;
import com.fitassist.backend.service.declaration.exercise.ExerciseService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.ExerciseSpecification;
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

import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {

	private final ValidationService validationService;

	private final JsonPatchService jsonPatchService;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final ExerciseMapper exerciseMapper;

	private final PlanMapper planMapper;

	private final RepositoryHelper repositoryHelper;

	private final ExerciseRepository exerciseRepository;

	private final PlanRepository planRepository;

	private final EquipmentRepository equipmentRepository;

	private final ExpertiseLevelRepository expertiseLevelRepository;

	private final ForceTypeRepository forceTypeRepository;

	private final MechanicsTypeRepository mechanicsTypeRepository;

	private final TargetMuscleRepository targetMuscleRepository;

	private final ExercisePopulationService exercisePopulationService;

	private final PlanPopulationService planPopulationService;

	private final SpecificationDependencies dependencies;

	public ExerciseServiceImpl(ExerciseMapper exerciseMapper, PlanMapper planMapper,
			ValidationService validationService, JsonPatchService jsonPatchService,
			ApplicationEventPublisher applicationEventPublisher, RepositoryHelper repositoryHelper,
			ExerciseRepository exerciseRepository, PlanRepository planRepository,
			EquipmentRepository equipmentRepository, ExpertiseLevelRepository expertiseLevelRepository,
			ForceTypeRepository forceTypeRepository, MechanicsTypeRepository mechanicsTypeRepository,
			TargetMuscleRepository targetMuscleRepository, ExercisePopulationService exercisePopulationService,
			PlanPopulationService planPopulationService, SpecificationDependencies dependencies) {
		this.exerciseMapper = exerciseMapper;
		this.planMapper = planMapper;
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.applicationEventPublisher = applicationEventPublisher;
		this.repositoryHelper = repositoryHelper;
		this.exerciseRepository = exerciseRepository;
		this.planRepository = planRepository;
		this.equipmentRepository = equipmentRepository;
		this.expertiseLevelRepository = expertiseLevelRepository;
		this.forceTypeRepository = forceTypeRepository;
		this.mechanicsTypeRepository = mechanicsTypeRepository;
		this.targetMuscleRepository = targetMuscleRepository;
		this.exercisePopulationService = exercisePopulationService;
		this.planPopulationService = planPopulationService;
		this.dependencies = dependencies;
	}

	@Override
	@Transactional
	public ExerciseResponseDto createExercise(ExerciseCreateDto dto) {
		ExerciseMappingContext context = prepareCreateContext(dto);
		Exercise saved = exerciseRepository.save(exerciseMapper.toEntity(dto, context));

		exerciseRepository.flush();

		Exercise exerciseWithMediaAndCategories = exerciseRepository.findByIdWithAssociationsForIndexing(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Exercise.class, saved.getId()));
		applicationEventPublisher.publishEvent(ExerciseCreateEvent.of(this, exerciseWithMediaAndCategories));

		return findAndMap(saved.getId());
	}

	private ExerciseMappingContext prepareCreateContext(ExerciseCreateDto dto) {
		ExpertiseLevel expertiseLevel = findExpertiseLevel(dto.getExpertiseLevelId());
		MechanicsType mechanicsType = findMechanicsType(dto.getMechanicsTypeId());
		ForceType forceType = findForceType(dto.getForceTypeId());
		Equipment equipment = findEquipment(dto.getEquipmentId());
		List<TargetMuscle> targetMuscles = findTargetMuscles(dto.getTargetMusclesIds());

		return new ExerciseMappingContext(expertiseLevel, mechanicsType, forceType, equipment, targetMuscles);
	}

	private ExerciseResponseDto findAndMap(int exerciseId) {
		Exercise exercise = exerciseRepository.findByIdWithDetails(exerciseId)
			.orElseThrow(() -> RecordNotFoundException.of(Exercise.class, exerciseId));
		ExerciseResponseDto dto = exerciseMapper.toResponse(exercise);
		exercisePopulationService.populate(dto);

		return dto;
	}

	@Override
	@Transactional
	public void updateExercise(int exerciseId, JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		Exercise exercise = exerciseRepository.findByIdWithDetails(exerciseId)
			.orElseThrow(() -> RecordNotFoundException.of(Exercise.class, exerciseId));
		ExerciseUpdateDto patchedExerciseUpdateDto = applyPatchToExercise(patch);

		validationService.validate(patchedExerciseUpdateDto);

		ExerciseMappingContext context = prepareUpdateContext(patchedExerciseUpdateDto);
		exerciseMapper.update(exercise, patchedExerciseUpdateDto, context);
		Exercise saved = exerciseRepository.save(exercise);

		Exercise exerciseWithMediaAndCategories = exerciseRepository.findByIdWithAssociationsForIndexing(saved.getId())
			.orElseThrow(() -> RecordNotFoundException.of(Exercise.class, saved.getId()));

		applicationEventPublisher.publishEvent(ExerciseUpdateEvent.of(this, exerciseWithMediaAndCategories));
	}

	private ExerciseUpdateDto applyPatchToExercise(JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, ExerciseUpdateDto.class);
	}

	private ExerciseMappingContext prepareUpdateContext(ExerciseUpdateDto dto) {
		ExpertiseLevel expertiseLevel = findExpertiseLevel(dto.getExpertiseLevelId());
		MechanicsType mechanicsType = findMechanicsType(dto.getMechanicsTypeId());
		ForceType forceType = findForceType(dto.getForceTypeId());
		Equipment equipment = findEquipment(dto.getEquipmentId());
		List<TargetMuscle> targetMuscles = findTargetMuscles(dto.getTargetMuscleIds());

		return new ExerciseMappingContext(expertiseLevel, mechanicsType, forceType, equipment, targetMuscles);
	}

	@Override
	@Transactional
	public void deleteExercise(int exerciseId) {
		Exercise exercise = find(exerciseId);
		exerciseRepository.delete(exercise);

		applicationEventPublisher.publishEvent(ExerciseDeleteEvent.of(this, exercise));
	}

	private Exercise find(int exerciseId) {
		return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
	}

	@Override
	@Cacheable(value = CacheNames.EXERCISES, key = "#exerciseId")
	public ExerciseResponseDto getExercise(int exerciseId) {
		ExerciseResponseDto dto = findAndMap(exerciseId);

		List<PlanSummaryDto> planSummaries = planRepository.findByExerciseIdWithDetails(exerciseId)
			.stream()
			.map(planMapper::toSummary)
			.toList();
		planPopulationService.populate(planSummaries);
		dto.setPlans(planSummaries);

		return dto;
	}

	@Override
	public Page<ExerciseSummaryDto> getFilteredExercises(FilterDto filter, Pageable pageable) {
		SpecificationFactory<Exercise> exerciseFactory = ExerciseSpecification::new;
		SpecificationBuilder<Exercise> specificationBuilder = SpecificationBuilder.of(filter, exerciseFactory,
				dependencies);
		Specification<Exercise> specification = specificationBuilder.build();

		Page<Exercise> exercisePage = exerciseRepository.findAll(specification, pageable);
		List<ExerciseSummaryDto> summaries = exercisePage.getContent().stream().map(exerciseMapper::toSummary).toList();
		exercisePopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, exercisePage.getTotalElements());
	}

	@Override
	public List<Exercise> getAllExerciseEntities() {
		return exerciseRepository.findAll();
	}

	@Override
	public ExerciseCategoriesResponseDto getAllExerciseCategories() {
		List<CategoryResponseDto> equipments = equipmentRepository.findAll()
			.stream()
			.map(equipment -> new CategoryResponseDto(equipment.getId(), equipment.getName()))
			.toList();

		List<CategoryResponseDto> expertiseLevels = expertiseLevelRepository.findAll()
			.stream()
			.map(expertiseLevel -> new CategoryResponseDto(expertiseLevel.getId(), expertiseLevel.getName()))
			.toList();

		List<CategoryResponseDto> forceTypes = forceTypeRepository.findAll()
			.stream()
			.map(forceType -> new CategoryResponseDto(forceType.getId(), forceType.getName()))
			.toList();

		List<CategoryResponseDto> mechanicsTypes = mechanicsTypeRepository.findAll()
			.stream()
			.map(mechanicsType -> new CategoryResponseDto(mechanicsType.getId(), mechanicsType.getName()))
			.toList();

		List<CategoryResponseDto> targetMuscles = targetMuscleRepository.findAll()
			.stream()
			.map(targetMuscle -> new CategoryResponseDto(targetMuscle.getId(), targetMuscle.getName()))
			.toList();

		return new ExerciseCategoriesResponseDto(equipments, expertiseLevels, forceTypes, mechanicsTypes,
				targetMuscles);
	}

	private ExpertiseLevel findExpertiseLevel(Integer expertiseLevelId) {
		if (expertiseLevelId == null) {
			return null;
		}
		return expertiseLevelRepository.findById(expertiseLevelId)
			.orElseThrow(() -> RecordNotFoundException.of(ExpertiseLevel.class, expertiseLevelId));
	}

	private MechanicsType findMechanicsType(Integer mechanicsTypeId) {
		if (mechanicsTypeId == null) {
			return null;
		}
		return mechanicsTypeRepository.findById(mechanicsTypeId)
			.orElseThrow(() -> RecordNotFoundException.of(MechanicsType.class, mechanicsTypeId));
	}

	private ForceType findForceType(Integer forceTypeId) {
		if (forceTypeId == null) {
			return null;
		}
		return forceTypeRepository.findById(forceTypeId)
			.orElseThrow(() -> RecordNotFoundException.of(ForceType.class, forceTypeId));
	}

	private Equipment findEquipment(Integer equipmentId) {
		if (equipmentId == null) {
			return null;
		}
		return equipmentRepository.findById(equipmentId)
			.orElseThrow(() -> RecordNotFoundException.of(Equipment.class, equipmentId));
	}

	private List<TargetMuscle> findTargetMuscles(List<Integer> targetMuscleIds) {
		if (targetMuscleIds == null || targetMuscleIds.isEmpty()) {
			return null;
		}
		return targetMuscleRepository.findAllById(targetMuscleIds);
	}

}

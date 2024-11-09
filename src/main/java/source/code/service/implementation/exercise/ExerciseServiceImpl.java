package source.code.service.implementation.exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import source.code.dto.request.exercise.ExerciseCreateDto;
import source.code.dto.request.exercise.ExerciseUpdateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.event.events.Exercise.ExerciseCreateEvent;
import source.code.event.events.Exercise.ExerciseDeleteEvent;
import source.code.event.events.Exercise.ExerciseUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;
import source.code.repository.ExerciseRepository;
import source.code.repository.ExerciseTargetMuscleRepository;
import source.code.service.declaration.exercise.ExerciseService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ExerciseSpecification;

import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExerciseMapper exerciseMapper;
    private final RepositoryHelper repositoryHelper;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseTargetMuscleRepository exerciseTargetMuscleRepository;

    public ExerciseServiceImpl(ExerciseMapper exerciseMapper,
                               ValidationService validationService,
                               JsonPatchService jsonPatchService,
                               ApplicationEventPublisher applicationEventPublisher,
                               RepositoryHelper repositoryHelper,
                               ExerciseRepository exerciseRepository,
                               ExerciseTargetMuscleRepository exerciseTargetMuscleRepository) {
        this.exerciseMapper = exerciseMapper;
        this.validationService = validationService;
        this.jsonPatchService = jsonPatchService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.repositoryHelper = repositoryHelper;
        this.exerciseRepository = exerciseRepository;
        this.exerciseTargetMuscleRepository = exerciseTargetMuscleRepository;
    }

    @Override
    @Transactional
    public ExerciseResponseDto createExercise(ExerciseCreateDto dto) {
        Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
        applicationEventPublisher.publishEvent(ExerciseCreateEvent.of(this, exercise));

        return exerciseMapper.toResponseDto(exercise);
    }

    @Override
    @Transactional
    public void updateExercise(int exerciseId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Exercise exercise = find(exerciseId);
        ExerciseUpdateDto patchedExerciseUpdateDto = applyPatchToExercise(exercise, patch);

        validationService.validate(patchedExerciseUpdateDto);
        exerciseMapper.updateExerciseFromDto(exercise, patchedExerciseUpdateDto);
        Exercise savedExercise = exerciseRepository.save(exercise);

        applicationEventPublisher.publishEvent(ExerciseUpdateEvent.of(this, savedExercise));
    }

    @Override
    @Transactional
    public void deleteExercise(int exerciseId) {
        Exercise exercise = find(exerciseId);
        exerciseRepository.delete(exercise);

        applicationEventPublisher.publishEvent(ExerciseDeleteEvent.of(this, exercise));
    }

    @Override
    @Cacheable(value = CacheNames.EXERCISES, key = "#exerciseId")
    public ExerciseResponseDto getExercise(int exerciseId) {
        Exercise exercise = find(exerciseId);
        return exerciseMapper.toResponseDto(exercise);
    }

    @Override
    @Cacheable(value = CacheNames.ALL_EXERCISES)
    public List<ExerciseResponseDto> getAllExercises() {
        return repositoryHelper.findAll(exerciseRepository, exerciseMapper::toResponseDto);
    }

    @Override
    public List<ExerciseResponseDto> getFilteredExercises(FilterDto filter) {
        SpecificationFactory<Exercise> exerciseFactory = ExerciseSpecification::of;
        SpecificationBuilder<Exercise> specificationBuilder = SpecificationBuilder.of(
                filter,
                exerciseFactory
        );
        Specification<Exercise> specification = specificationBuilder.build();

        return exerciseRepository.findAll(specification).stream()
                .map(exerciseMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<Exercise> getAllExerciseEntities() {
        return exerciseRepository.findAllWithoutAssociations();
    }

    @Override
    @Cacheable(value = CacheNames.EXERCISES_BY_CATEGORY, key = "#categoryId")
    public List<ExerciseResponseDto> getExercisesByCategory(int categoryId) {
        return exerciseTargetMuscleRepository.findByTargetMuscleId(categoryId).stream()
                .map(ExerciseTargetMuscle::getExercise)
                .map(exerciseMapper::toResponseDto)
                .toList();
    }

    private Exercise find(int exerciseId) {
        return repositoryHelper.find(exerciseRepository, Exercise.class, exerciseId);
    }

    private ExerciseUpdateDto applyPatchToExercise(Exercise exercise, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        ExerciseResponseDto responseDto = exerciseMapper.toResponseDto(exercise);
        return jsonPatchService.applyPatch(patch, responseDto, ExerciseUpdateDto.class);
    }
}
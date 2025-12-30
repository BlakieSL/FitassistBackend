package source.code.service.implementation.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.request.workout.WorkoutUpdateDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.WorkoutMapper;
import source.code.model.workout.Workout;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.workout.WorkoutService;

@Service
public class WorkoutServiceImpl implements WorkoutService {

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final WorkoutMapper workoutMapper;

	private final RepositoryHelper repositoryHelper;

	private final WorkoutRepository workoutRepository;

	public WorkoutServiceImpl(JsonPatchService jsonPatchService, ValidationService validationService,
			WorkoutMapper workoutMapper, RepositoryHelper repositoryHelper, WorkoutRepository workoutRepository) {
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.workoutMapper = workoutMapper;
		this.repositoryHelper = repositoryHelper;
		this.workoutRepository = workoutRepository;
	}

	@Override
	@Transactional
	public WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto) {
		Workout saved = workoutRepository.save(workoutMapper.toEntity(workoutDto));
		workoutRepository.flush();
		return findAndMap(saved.getId());
	}

	@Override
	@Transactional
	public void updateWorkout(int workoutId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Workout workout = find(workoutId);
		WorkoutUpdateDto patched = applyPatchToWorkout(patch);

		validationService.validate(patched);
		workoutMapper.updateWorkout(workout, patched);
		workoutRepository.save(workout);
	}

	@Override
	@Transactional
	public void deleteWorkout(int workoutId) {
		Workout workout = find(workoutId);
		workoutRepository.delete(workout);
	}

	@Override
	public WorkoutResponseDto getWorkout(int id) {
		return findAndMap(id);
	}

	@Override
	public List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId) {
		return workoutRepository.findAllByPlanId(planId).stream().map(workoutMapper::toResponseDto).toList();
	}

	private Workout find(int workoutId) {
		return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
	}

	private WorkoutResponseDto findAndMap(int workoutId) {
		Workout workout = workoutRepository.findByIdWithDetails(workoutId)
			.orElseThrow(() -> new RecordNotFoundException(Workout.class, workoutId));
		return workoutMapper.toResponseDto(workout);
	}

	private WorkoutUpdateDto applyPatchToWorkout(JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, WorkoutUpdateDto.class);
	}

}

package source.code.service.implementation.workoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.WorkoutSetMapper;
import source.code.model.workout.WorkoutSet;
import source.code.repository.WorkoutSetRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.workoutSet.WorkoutSetService;

@Service
public class WorkoutSetServiceImpl implements WorkoutSetService {

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final RepositoryHelper repositoryHelper;

	private final WorkoutSetRepository workoutSetRepository;

	private final WorkoutSetMapper workoutSetMapper;

	public WorkoutSetServiceImpl(JsonPatchService jsonPatchService, ValidationService validationService,
								 RepositoryHelper repositoryHelper, WorkoutSetRepository workoutSetRepository,
								 WorkoutSetMapper workoutSetMapper) {
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.repositoryHelper = repositoryHelper;
		this.workoutSetRepository = workoutSetRepository;
		this.workoutSetMapper = workoutSetMapper;
	}

	@Transactional
	@Override
	public WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto createDto) {
		WorkoutSet saved = workoutSetRepository.save(workoutSetMapper.toEntity(createDto));

		workoutSetRepository.flush();

		return findAndMap(saved.getId());
	}

	@Transactional
	@Override
	public void updateWorkoutSet(int workoutSetId, JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		WorkoutSet workoutSet = find(workoutSetId);
		WorkoutSetUpdateDto patched = applyPatchToWorkoutSet(patch);

		validationService.validate(patched);
		workoutSetMapper.updateWorkoutSet(workoutSet, patched);
		workoutSetRepository.save(workoutSet);
	}

	@Transactional
	@Override
	public void deleteWorkoutSet(int workoutSetId) {
		WorkoutSet workoutSet = find(workoutSetId);
		workoutSetRepository.delete(workoutSet);
	}

	@Override
	public WorkoutSetResponseDto getWorkoutSet(int workoutSetId) {
		return findAndMap(workoutSetId);
	}

	@Override
	public List<WorkoutSetResponseDto> getAllWorkoutSetsForWorkout(int workoutId) {
		return workoutSetRepository.findAllByWorkoutId(workoutId)
			.stream()
			.map(workoutSetMapper::toResponseDto)
			.toList();
	}

	private WorkoutSet find(int workoutSetId) {
		return repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId);
	}

	private WorkoutSetResponseDto findAndMap(int workoutSetId) {
		WorkoutSet workoutSet = workoutSetRepository.findByIdWithDetails(workoutSetId)
			.orElseThrow(() -> new RecordNotFoundException(WorkoutSet.class, workoutSetId));
		return workoutSetMapper.toResponseDto(workoutSet);
	}

	private WorkoutSetUpdateDto applyPatchToWorkoutSet(JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, WorkoutSetUpdateDto.class);
	}

}

package source.code.service;

import source.code.dto.request.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.ExerciseMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.model.*;
import source.code.repository.*;
import source.code.service.interfaces.ExerciseService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ValidationHelper validationHelper;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;
    private final ExerciseTipRepository exerciseTipRepository;
    public ExerciseServiceImpl(ValidationHelper validationHelper,
                               ExerciseMapper exerciseMapper,
                               ExerciseRepository exerciseRepository,
                               UserExerciseRepository userExerciseRepository,
                               ExerciseCategoryRepository exerciseCategoryRepository,
                               ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository, ExerciseInstructionRepository exerciseInstructionRepository, ExerciseTipRepository exerciseTipRepository) {
        this.validationHelper = validationHelper;
        this.exerciseMapper = exerciseMapper;
        this.exerciseRepository = exerciseRepository;
        this.userExerciseRepository = userExerciseRepository;
        this.exerciseCategoryRepository = exerciseCategoryRepository;
        this.exerciseCategoryAssociationRepository = exerciseCategoryAssociationRepository;
        this.exerciseInstructionRepository = exerciseInstructionRepository;
        this.exerciseTipRepository = exerciseTipRepository;
    }

    @Transactional
    public ExerciseResponseDto createExercise(ExerciseCreateDto dto) {
        validationHelper.validate(dto);
        Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(dto));
        return exerciseMapper.toDto(exercise);
    }

    public ExerciseResponseDto getExercise(int id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " + id + " not found"));
        return exerciseMapper.toDto(exercise);
    }

    public List<ExerciseResponseDto> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByUser(int userId) {
        List<UserExercise> userExercises = userExerciseRepository
                .findByUserId(userId);
        List<Exercise> exercises = userExercises
                .stream()
                .map(UserExercise::getExercise)
                .collect(Collectors.toList());
        return exercises
                .stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> searchExercises(SearchRequestDto dto){
        List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(dto.getName());
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseCategoryResponseDto> getAllCategories() {
        List<ExerciseCategory> categories = exerciseCategoryRepository.findAll();
        return categories.stream()
                .map(exerciseMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByCategory(int categoryId) {
        List<ExerciseCategoryAssociation> exerciseCategoryAssociations = exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId);
        List<Exercise> exercises = exerciseCategoryAssociations.stream()
                .map(ExerciseCategoryAssociation::getExercise)
                .collect(Collectors.toList());
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByExpertiseLevel(int expertiseLevelId) {
        List<Exercise> exercises = exerciseRepository.findByExpertiseLevel_Id(expertiseLevelId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ExerciseResponseDto> getExercisesByForceType(int forceTypeId) {
        List<Exercise> exercises = exerciseRepository.findByForceType_Id(forceTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByMechanicsType(int mechanicsTypeId) {
        List<Exercise> exercises = exerciseRepository.findByMechanicsType_Id(mechanicsTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByEquipment(int exerciseEquipmentId) {
        List<Exercise> exercises = exerciseRepository.findByExerciseEquipment_Id(exerciseEquipmentId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponseDto> getExercisesByType(int exerciseTypeId) {
        List<Exercise> exercises = exerciseRepository.findByExerciseType_Id(exerciseTypeId);
        return exercises.stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseInstructionResponseDto> getExerciseInstructions(int exerciseId) {
        List<ExerciseInstruction> instructions = exerciseInstructionRepository.getAllByExerciseId(exerciseId);
        return instructions.stream()
                .map(exerciseMapper::toInstructionDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseTipResponseDto> getExerciseTips(int exerciseId) {
     List<ExerciseTip> tips = exerciseTipRepository.getAllByExerciseId(exerciseId);
        return tips.stream()
                .map(exerciseMapper::toTipDto)
                .collect(Collectors.toList());
    }
}


package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.Exercise.ExerciseCreateDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ExerciseCategoryResponseDto;
import source.code.dto.response.ExerciseResponseDto;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.*;
import source.code.model.User.User;
import source.code.model.User.UserExercise;
import source.code.repository.*;
import source.code.service.implementation.Exercise.ExerciseServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {
  @Mock
  private ExerciseMapper exerciseMapper;
  @Mock
  private ExerciseRepository exerciseRepository;
  @Mock
  private UserExerciseRepository userExerciseRepository;
  @Mock
  private ExerciseCategoryRepository exerciseCategoryRepository;
  @Mock
  private ExerciseCategoryAssociationRepository exerciseCategoryAssociationRepository;
  @InjectMocks
  private ExerciseServiceImpl exerciseService;
  private User user1;
  private User user2;
  private UserExercise userExercise1;
  private UserExercise userExercise2;
  private UserExercise userExercise3;
  private UserExercise userExercise4;
  private UserExercise userExercise5;
  private UserExercise userExercise6;
  private ExerciseCategory category1;
  private ExerciseCategory category2;
  private ExerciseCategory category3;
  private ExerciseCategory category4;
  private Exercise exercise1;
  private Exercise exercise2;
  private Exercise exercise3;
  private ExerciseCategoryAssociation association1;
  private ExerciseCategoryAssociation association2;
  private ExerciseCategoryAssociation association3;
  private ExerciseCategoryAssociation association4;
  private ExerciseResponseDto exerciseResponseDto1;
  private ExerciseResponseDto exerciseResponseDto2;
  private ExerciseResponseDto exerciseResponseDto3;
  private ExerciseCategoryResponseDto categoryResponseDto1;
  private ExerciseCategoryResponseDto categoryResponseDto2;
  private ExerciseCategoryResponseDto categoryResponseDto3;
  private ExerciseCategoryResponseDto categoryResponseDto4;
  private ExerciseCreateDto createDto1;
  private ExerciseCreateDto createDto2;
  private ExerciseCreateDto createDto3;
  @BeforeEach
  void setup() {
    String exerciseName1 = "Exercise1";
    String exerciseName2 = "Exercise2";
    String exerciseName3 = "Exercise3";

    user1 = createUser(1);
    user2 = createUser(2);

    category1 = createCategory(1);
    category2 = createCategory(2);
    category3 = createCategory(3);
    category4 = createCategory(4);

    exercise1 = createExercise(1, exerciseName1);
    exercise2 = createExercise(2, exerciseName2);
    exercise3 = createExercise(3, exerciseName3);

    association1 = createAssociation(1, exercise1, category1);
    association2 = createAssociation(2, exercise2, category2);
    association3 = createAssociation(3, exercise3, category3);
    association4 = createAssociation(4, exercise3, category4);

    exerciseResponseDto1 = createResponseDto(exercise1.getId(), exercise1.getName());
    exerciseResponseDto2 = createResponseDto(exercise2.getId(), exercise2.getName());
    exerciseResponseDto3 = createResponseDto(exercise3.getId(), exercise3.getName());

    categoryResponseDto1 = createCategoryResponseDto(category1.getId());
    categoryResponseDto2 = createCategoryResponseDto(category2.getId());
    categoryResponseDto3 = createCategoryResponseDto(category3.getId());
    categoryResponseDto4 = createCategoryResponseDto(category4.getId());

    createDto1 = createExerciseCreateDto(exerciseName1);
    createDto2 = createExerciseCreateDto(exerciseName2);
    createDto3 = createExerciseCreateDto(exerciseName3);

    userExercise1 = createUserExercise(1, user1, exercise1, (short) 1);
    userExercise2 = createUserExercise(2, user1, exercise1, (short) 2);
    userExercise3 = createUserExercise(3, user1, exercise2, (short) 1);

    userExercise4 = createUserExercise(4, user2, exercise3, (short) 1);
    userExercise5 = createUserExercise(5, user2, exercise3, (short) 2);
    userExercise6 = createUserExercise(6, user2, exercise2, (short) 2);
  }

  private User createUser(int id) {
    return User.createWithId(id);
  }

  private UserExercise createUserExercise(int id, User user, Exercise exercise, short type) {
    return new UserExercise(id, user, exercise, type);
  }

  private ExerciseCategory createCategory(int id) {
    return ExerciseCategory.createWithId(id);
  }

  private ExerciseCategoryResponseDto createCategoryResponseDto(int id) {
    return ExerciseCategoryResponseDto.createWithId(id);
  }

  private ExerciseCreateDto createExerciseCreateDto(String name) {
    return ExerciseCreateDto.createWithName(name);
  }

  private Exercise createExercise(int id, String name) {
    return Exercise.createWithIdAndName(id, name);
  }

  private ExerciseResponseDto createResponseDto(int id, String name) {
    return ExerciseResponseDto.createWithIdAndName(id, name);
  }

  private ExerciseCategoryAssociation createAssociation(
          int id,Exercise exercise, ExerciseCategory exerciseCategory) {
    return ExerciseCategoryAssociation
            .createWithIdAndExerciseAndExerciseCategory(id, exercise, exerciseCategory);
  }

  @Test
  void createExercise_shouldCreate() {
    // Arrange
    when(exerciseMapper.toEntity(createDto1)).thenReturn(exercise1);
    when(exerciseRepository.save(exercise1)).thenReturn(exercise1);
    when(exerciseMapper.toResponseDto(exercise1)).thenReturn(exerciseResponseDto1);

    // Act
    ExerciseResponseDto result = exerciseService.createExercise(createDto1);

    // Assert
    verify(exerciseMapper, times(1)).toEntity(createDto1);
    verify(exerciseRepository, times(1)).save(exercise1);
    verify(exerciseMapper, times(1)).toResponseDto(exercise1);
    assertEquals(exerciseResponseDto1.getId(), result.getId());
    assertEquals(createDto1.getName(), result.getName());
  }

  @Test
  void getExercise_shouldReturnExerciseResponseDto_whenExerciseFound() {
    // Arrange
    int exerciseId = exercise1.getId();
    when(exerciseRepository.findById(exerciseId)).thenReturn(java.util.Optional.of(exercise1));
    when(exerciseMapper.toResponseDto(exercise1)).thenReturn(exerciseResponseDto1);

    // Act
    ExerciseResponseDto result = exerciseService.getExercise(exerciseId);

    // Assert
    verify(exerciseRepository, times(1)).findById(exerciseId);
    verify(exerciseMapper, times(1)).toResponseDto(exercise1);
    assertEquals(exerciseResponseDto1, result);
  }

  @Test
  void getExercise_shouldNotMap_whenExerciseNotFound() {
  // Arrange
    int exerciseId = exercise1.getId();
    when(exerciseRepository.findById(exerciseId)).thenReturn(java.util.Optional.empty());

    // Act & Assert
    NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> exerciseService.getExercise(exerciseId));

    assertEquals("Exercise with id: " + exerciseId + " not found", exception.getMessage());
    verify(exerciseRepository, times(1)).findById(exerciseId);
    verify(exerciseMapper, never()).toResponseDto(any());
  }

  @Test
  void getAllExercises_shouldReturnAllExercises_whenExercisesFound() {
    // Arrange
    List<Exercise> exercises = List.of(exercise1, exercise2, exercise3);
    List<ExerciseResponseDto> responseDtos = List.of(exerciseResponseDto1, exerciseResponseDto2, exerciseResponseDto3);

    when(exerciseRepository.findAll()).thenReturn(exercises);
    when(exerciseMapper.toResponseDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toResponseDto(exercise2)).thenReturn(exerciseResponseDto2);
    when(exerciseMapper.toResponseDto(exercise3)).thenReturn(exerciseResponseDto3);
    // Act
    List<ExerciseResponseDto> result = exerciseService.getAllExercises();

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toResponseDto(exercise1);
    verify(exerciseMapper, times(1)).toResponseDto(exercise2);
    verify(exerciseMapper, times(1)).toResponseDto(exercise3);
    assertEquals(responseDtos.get(0).getId(), result.get(0).getId());
    assertEquals(responseDtos.get(1).getId(), result.get(1).getId());
    assertEquals(responseDtos.get(2).getId(), result.get(2).getId());
  }

  @Test
  void getAllExercises_shouldReturnEmptyList_whenExerciseNotFound() {
    // Arrange
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getAllExercises();

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }

  /*
  @Test
  void getExercisesByUser_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    int userId = user1.getId();

    List<UserExercise> userExercises = List.of(userExercise1, userExercise2, userExercise3);
    when(userExerciseRepository.findByUserId(userId)).thenReturn(userExercises);
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise2)).thenReturn(exerciseResponseDto2);
    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByUserAndType(userId);

    // Assert
    verify(userExerciseRepository, times(1)).findByUserId(userId);
    verify(exerciseMapper, times(2)).toDto(exercise1);
    verify(exerciseMapper, times(1)).toDto(exercise2);

    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
    assertEquals(exerciseResponseDto1.getId(), result.get(1).getId());
    assertEquals(exerciseResponseDto2.getId(), result.get(2).getId());
  }

  @Test
  void getExercisesByUser_shouldReturnEmptyList_whenExercisesNotFound() {
    // Arrange
    int nonExistingUserId = 99;
    when(userExerciseRepository.findByUserId(nonExistingUserId)).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByUserAndType(nonExistingUserId);

    // Assert
    verify(userExerciseRepository, times(1)).findByUserId(nonExistingUserId);
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }


   */
  @Test
  void searchExercises_shouldReturnExerciseResponseDto_whenExercisesFound() {
    // Arrange
    String searchQuery = "Exercise1";
    SearchRequestDto searchDto = new SearchRequestDto(searchQuery);

    when(exerciseRepository.findByNameContainingIgnoreCase(searchQuery)).thenReturn(List.of(exercise1));
    when(exerciseMapper.toResponseDto(exercise1)).thenReturn(exerciseResponseDto1);

    // Act
    List<ExerciseResponseDto> result = exerciseService.searchExercises(searchDto);

    // Assert
    verify(exerciseRepository, times(1)).findByNameContainingIgnoreCase(searchQuery);
    verify(exerciseMapper, times(1)).toResponseDto(exercise1);
    assertEquals(1, result.size());

    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
  }

  @Test
  void searchExercises_shouldReturnEmptyList_whenExercisesNotFound() {
    // Arrange
    String searchQuery = "NonExistingExercise";
    SearchRequestDto searchDto = new SearchRequestDto(searchQuery);
    when(exerciseRepository.findByNameContainingIgnoreCase(searchQuery)).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.searchExercises(searchDto);

    // Assert
    verify(exerciseRepository, times(1)).findByNameContainingIgnoreCase(searchQuery);
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }

  /*
  @Test
  void getAllCategories_shouldReturnAllCategories_whenCategoriesFound() {
    // Arrange
    when(exerciseCategoryRepository.findAll()).thenReturn(List.of(category1, category2, category3, category4));
    when(exerciseMapper.toCategoryDto(category1)).thenReturn(categoryResponseDto1);
    when(exerciseMapper.toCategoryDto(category2)).thenReturn(categoryResponseDto2);
    when(exerciseMapper.toCategoryDto(category3)).thenReturn(categoryResponseDto3);
    when(exerciseMapper.toCategoryDto(category4)).thenReturn(categoryResponseDto4);

    // Act
    List<ExerciseCategoryResponseDto> result = exerciseService.getAllCategories();

    // Assert
    verify(exerciseCategoryRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toCategoryDto(category1);
    verify(exerciseMapper, times(1)).toCategoryDto(category2);
    verify(exerciseMapper, times(1)).toCategoryDto(category3);
    verify(exerciseMapper, times(1)).toCategoryDto(category4);

    assertEquals(4, result.size());
    assertEquals(categoryResponseDto1.getId(), result.get(0).getId());
    assertEquals(categoryResponseDto2.getId(), result.get(1).getId());
    assertEquals(categoryResponseDto3.getId(), result.get(2).getId());
    assertEquals(categoryResponseDto4.getId(), result.get(3).getId());
  }

  @Test
  void getAllCategories_shouldReturnEmptyList_whenCategoriesNotFound() {
    // Arrange
    when(exerciseCategoryRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseCategoryResponseDto> result = exerciseService.getAllCategories();

    // Assert
    verify(exerciseCategoryRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }
   */
  @Test
  void getExercisesByCategory_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    int categoryId = category1.getId();
    when(exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId)).thenReturn(List.of(association1));
    when(exerciseMapper.toResponseDto(exercise1)).thenReturn(exerciseResponseDto1);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByCategory(categoryId);

    // Assert
    verify(exerciseCategoryAssociationRepository, times(1)).findByExerciseCategoryId(categoryId);
    verify(exerciseMapper, times(1)).toResponseDto(exercise1);
    assertEquals(1, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
  }

  @Test
  void getExercisesByCategory_shouldReturnEmptyList_whenNoExercisesFound() {
    // Arrange
    int categoryId = category1.getId();
    when(exerciseCategoryAssociationRepository.findByExerciseCategoryId(categoryId)).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByCategory(categoryId);

    // Assert
    verify(exerciseCategoryAssociationRepository, times(1)).findByExerciseCategoryId(categoryId);
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }
/*
  @Test
  void getExercisesByExpertiseLevel_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    ExpertiseLevel expertiseLevel = ExpertiseLevel.createWithId(1);
    exercise1.setExpertiseLevel(expertiseLevel);

    when(exerciseRepository.findAll()).thenReturn(List.of(exercise1));
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByExpertiseLevel(expertiseLevel.getId());

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toDto(exercise1);
    assertEquals(1, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
  }

  @Test
  void getExercisesByExpertiseLevel_shouldReturnEmptyList_whenNoExercisesFound() {
    // Arrange
    int expertiseLevelId = 1;
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByExpertiseLevel(expertiseLevelId);

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }

  @Test
  void getExercisesByForceType_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    ForceType forceType = ForceType.createWithId(1);
    exercise1.setForceType(forceType);
    exercise2.setForceType(forceType);

    when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise2)).thenReturn(exerciseResponseDto2);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByForceType(forceType.getId());

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toDto(exercise1);
    verify(exerciseMapper, times(1)).toDto(exercise2);
    assertEquals(2, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
    assertEquals(exerciseResponseDto2.getId(), result.get(1).getId());
  }

  @Test
  void getExercisesByForceType_shouldReturnEmptyList_whenNoExercisesFound() {
    // Arrange
    int nonExistingForceTypeId = 1;
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByForceType(nonExistingForceTypeId);

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }

  @Test
  void getExercisesByMechanicsType_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    MechanicsType mechanicsType = MechanicsType.createWithId(1);
    exercise1.setMechanicsType(mechanicsType);
    exercise2.setMechanicsType(mechanicsType);

    when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise2)).thenReturn(exerciseResponseDto2);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByMechanicsType(mechanicsType.getId());

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toDto(exercise1);
    verify(exerciseMapper, times(1)).toDto(exercise2);
    assertEquals(2, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
    assertEquals(exerciseResponseDto2.getId(), result.get(1).getId());
  }

  @Test
  void getExercisesByMechanicsType_shouldReturnEmptyList_whenNoExercisesFound(){
    // Arrange
    int nonExistingMechanicsType = 1;
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByMechanicsType(nonExistingMechanicsType);

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }

  @Test
  void getExercisesByEquipment_shouldReturnExercises_whenExercisesFound() {
    // Arrange
    ExerciseEquipment equipment = ExerciseEquipment.createWithId(1);
    exercise1.setExerciseEquipment(equipment);
    exercise2.setExerciseEquipment(equipment);

    when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise2)).thenReturn(exerciseResponseDto2);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByEquipment(equipment.getId());

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toDto(exercise1);
    verify(exerciseMapper, times(1)).toDto(exercise2);
    assertEquals(2, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
    assertEquals(exerciseResponseDto2.getId(), result.get(1).getId());
  }


  @Test
  void getExercisesByEquipment_shouldReturnEmptyList_whenNoExercisesFound() {
    // Arrange
    int nonExistingEquipmentId = 1;
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByEquipment(nonExistingEquipmentId);

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }


  @Test
  void getExercisesByType_shouldReturnExercises_whenExercisesFound() {
    ExerciseType type = ExerciseType.createWithId(1);
    exercise1.setExerciseType(type);
    exercise2.setExerciseType(type);

    when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));
    when(exerciseMapper.toDto(exercise1)).thenReturn(exerciseResponseDto1);
    when(exerciseMapper.toDto(exercise2)).thenReturn(exerciseResponseDto2);

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByType(type.getId());

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verify(exerciseMapper, times(1)).toDto(exercise1);
    verify(exerciseMapper, times(1)).toDto(exercise2);
    assertEquals(2, result.size());
    assertEquals(exerciseResponseDto1.getId(), result.get(0).getId());
    assertEquals(exerciseResponseDto2.getId(), result.get(1).getId());
  }


  @Test
  void getExercisesByType_shouldReturnEmptyList_whenNoExercisesFound() {
    // Arrange
    int nonExistingTypeId = 1;
    when(exerciseRepository.findAll()).thenReturn(List.of());

    // Act
    List<ExerciseResponseDto> result = exerciseService.getExercisesByType(nonExistingTypeId);

    // Assert
    verify(exerciseRepository, times(1)).findAll();
    verifyNoInteractions(exerciseMapper);
    assertTrue(result.isEmpty());
  }
*/
}

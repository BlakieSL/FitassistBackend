package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.ValidationHelper;
import source.code.mapper.ExerciseMapper;
import source.code.repository.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {
  @Mock
  private ValidationHelper validationHelper;
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
  @Mock
  private ExerciseInstructionRepository exerciseInstructionRepository;
  @Mock
  private ExerciseTipRepository exerciseTipRepository;
  @InjectMocks
  private ExerciseServiceImpl exerciseService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createExercise_shouldCreate_whenValidationPassed() {

  }

  @Test
  void createExercise_shouldThrowException_whenValidationFails() {

  }

  @Test
  void getExercise_shouldReturnExerciseResponseDto_whenExerciseFound() {

  }

  @Test
  void getExercise_shouldNotMap_whenExerciseNotFound() {

  }

  @Test
  void getAllExercises_shouldReturnAllExercises_whenExercisesFound() {

  }

  @Test
  void getAllExercises_shouldReturnEmptyList_whenExerciseNotFound() {

  }

  @Test
  void getExercisesByUser_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByUser_shouldReturnEmptyList_whenExercisesNotFound() {

  }

  @Test
  void searchExercises_shouldReturnExerciseResponseDto_whenExercisesFound() {

  }

  @Test
  void searchExercises_shouldReturnEmptyList_whenExercisesNotFound() {

  }

  @Test
  void getAllCategories_shouldReturnAllCategories_whenCategoriesFound() {

  }

  @Test
  void getAllCategories_shouldReturnEmptyList_whenCategoriesNotFound() {

  }

  @Test
  void getExercisesByCategory_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByCategory_shouldReturnEmptyList_whenNoExercisesFound() {

  }

  @Test
  void getExercisesByExpertiseLevel_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByExpertiseLevel_shouldReturnEmptyList_whenNoExercisesFound() {

  }

  @Test
  void getExercisesByForceType_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByForceType_shouldReturnEmptyList_whenNoExercisesFound() {

  }

  @Test
  void getExercisesByMechanicsType_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByMechanicsType_shouldReturnEmptyList_whenNoExercisesFound(){

  }

  @Test
  void getExercisesByEquipment_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByEquipment_shouldReturnEmptyList_whenNoExercisesFound() {

  }

  @Test
  void getExercisesByType_shouldReturnExercises_whenExercisesFound() {

  }

  @Test
  void getExercisesByType_shouldReturnEmptyLIst_whenNoExercisesFound() {

  }

  @Test
  void getExerciseInstructions_shouldReturnInstructions_whenInstructionsFound() {

  }

  @Test
  void getExerciseInstructions_shouldReturnEmptyList_whenNoInstructionsFound() {

  }

  @Test
  void getExerciseTips_shouldReturnTips_whenTipsFound() {

  }

  @Test
  void getExerciseTips_shouldReturnEmptyList_whenNoTipsFound() {

  }
}

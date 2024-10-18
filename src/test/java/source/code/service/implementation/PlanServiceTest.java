package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.mapper.Plan.PlanMapper;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.service.implementation.Plan.PlanServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {
  @Mock
  private PlanMapper planMapper;
  @Mock
  private PlanRepository planRepository;
  @Mock
  private UserPlanRepository userPlanRepository;
  @Mock
  private PlanCategoryRepository planCategoryRepository;
  @Mock
  private PlanCategoryAssociationRepository planCategoryAssociationRepository;
  @InjectMocks
  private PlanServiceImpl planService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createPlan_shouldCreate() {

  }

  @Test
  void getPlan_shouldReturnPlan_whenPlanFound() {

  }

  @Test
  void getPlan_shouldThrowException_whenPlanNotFound() {

  }

  @Test
  void getAllPlans_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getAllPlans_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getPlansByUser_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByUser_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getAllCategories_shouldReturnCategories_whenCategoriesFound() {

  }

  @Test
  void getAllCategories_shouldReturnEmptyList_whenNoCategoriesFound() {

  }

  @Test
  void getPlansByCategory_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByCategory_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getPlansByType_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByType_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getPlansByDuration_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByDuration_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getPlansByEquipment_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByEquipment_shouldReturnEmptyList_whenNoPlansFound() {

  }

  @Test
  void getPlansByExpertiseLevel_shouldReturnPlans_whenPlansFound() {

  }

  @Test
  void getPlansByExpertiseLevel_shouldReturnEmptyList_whenNoPlansFound() {

  }
}

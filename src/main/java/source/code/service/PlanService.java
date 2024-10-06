package source.code.service;

import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.PlanMapper;
import source.code.model.Plan;
import source.code.model.PlanCategory;
import source.code.model.PlanCategoryAssociation;
import source.code.model.UserPlan;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanCategoryRepository;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PlanService {
    private final ValidationHelper validationHelper;
    private final PlanMapper planMapper;
    private final PlanRepository planRepository;
    private final UserPlanRepository userPlanRepository;
    private final PlanCategoryRepository planCategoryRepository;
    private final PlanCategoryAssociationRepository planCategoryAssociationRepository;

    public PlanService(ValidationHelper validationHelper,
                       PlanMapper planMapper,
                       PlanRepository planRepository,
                       UserPlanRepository userPlanRepository,
                       PlanCategoryRepository planCategoryRepository,
                       PlanCategoryAssociationRepository planCategoryAssociationRepository) {
        this.validationHelper = validationHelper;
        this.planMapper = planMapper;
        this.planRepository = planRepository;
        this.userPlanRepository = userPlanRepository;
        this.planCategoryRepository = planCategoryRepository;
        this.planCategoryAssociationRepository = planCategoryAssociationRepository;
    }

    @Transactional
    public PlanResponseDto createPlan(PlanCreateDto planDto) {
        validationHelper.validate(planDto);
        Plan plan = planRepository.save(planMapper.toEntity(planDto));

        return planMapper.toDto(plan);
    }

    public PlanResponseDto getPlan(int id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Plan with id: " + id + " not found"));

        return planMapper.toDto(plan);
    }

    public List<PlanResponseDto> getAllPlans() {
        List<Plan> plans = planRepository.findAll();

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByUser(int userId) {
        List<UserPlan> userPlans = userPlanRepository.findByUserId(userId);
        List<Plan> plans = userPlans.stream()
                .map(UserPlan::getPlan)
                .collect(Collectors.toList());

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanCategoryResponseDto> getAllCategories() {
        List<PlanCategory> categories = planCategoryRepository.findAll();

        return categories.stream()
                .map(planMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByCategory(int categoryId) {
        List<PlanCategoryAssociation> planCategoryAssociations = planCategoryAssociationRepository.findByPlanCategoryId(categoryId);
        List<Plan> plans = planCategoryAssociations.stream()
                .map(PlanCategoryAssociation::getPlan)
                .collect(Collectors.toList());

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByType(int planTypeId) {
        List<Plan> plans = planRepository.findByPlanType_Id(planTypeId);

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByDuration(int planDurationId) {
        List<Plan> plans = planRepository.findByPlanDuration_Id(planDurationId);

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByEquipment(int planEquipmentId) {
        List<Plan> plans = planRepository.findByPlanEquipment_Id(planEquipmentId);

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanResponseDto> getPlansByExpertiseLevel(int planExpertiseLevelId) {
        List<Plan> plans = planRepository.findByPlanExpertiseLevel_Id(planExpertiseLevelId);

        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }
}
package source.code.service.implementation.user.interaction.withType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.declaration.user.SavedService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userPlanService")
public class UserPlanServiceImpl
        extends GenericSavedService<Plan, UserPlan, PlanResponseDto>
        implements SavedService {

    private final PlanMapper planMapper;
    private final PlanPopulationService planPopulationService;

    public UserPlanServiceImpl(UserPlanRepository userPlanRepository,
                               PlanRepository planRepository,
                               UserRepository userRepository,
                               PlanMapper planMapper,
                               PlanPopulationService planPopulationService) {
        super(userRepository,
                planRepository,
                userPlanRepository,
                planMapper::toResponseDto,
                Plan.class,
                UserPlan.class);
        this.planMapper = planMapper;
        this.planPopulationService = planPopulationService;
    }

    @Override
    @CacheEvict(value = CacheNames.PLANS, key = "#entityId")
    public void saveToUser(int entityId, TypeOfInteraction type) {
        super.saveToUser(entityId, type);
    }

    @Override
    @CacheEvict(value = CacheNames.PLANS, key = "#entityId")
    public void deleteFromUser(int entityId, TypeOfInteraction type) {
        super.deleteFromUser(entityId, type);
    }

    @Override
    public Page<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
        Page<UserPlan> userPlanPage = ((UserPlanRepository) userEntityRepository)
                .findByUserIdAndTypeWithPlan(userId, type, pageable);

        if (userPlanPage.isEmpty()) return new PageImpl<>(List.of(), pageable, 0);

        List<Integer> planIds = userPlanPage.getContent().stream()
                .map(up -> up.getPlan().getId())
                .toList();

        List<Plan> plansWithDetails = ((PlanRepository) entityRepository).findByIdsWithDetails(planIds);

        Map<Integer, Plan> planMap = plansWithDetails.stream()
                .collect(Collectors.toMap(Plan::getId, p -> p));

        List<PlanSummaryDto> summaries = userPlanPage.getContent().stream()
                .map(up -> {
                    Plan plan = planMap.get(up.getPlan().getId());
                    PlanSummaryDto dto = planMapper.toSummaryDto(plan);
                    dto.setInteractedWithAt(up.getCreatedAt());
                    return dto;
                })
                .toList();

        planPopulationService.populate(summaries);

        return new PageImpl<>(summaries.stream().map(dto -> (BaseUserEntity) dto).toList(),
                pageable, userPlanPage.getTotalElements());
    }

    @Override
    protected boolean isAlreadySaved(int userId, int planId, TypeOfInteraction type) {
        return ((UserPlanRepository) userEntityRepository)
                .existsByUserIdAndPlanIdAndType(userId, planId, type);
    }

    @Override
    protected UserPlan createUserEntity(User user, Plan entity, TypeOfInteraction type) {
        if (!entity.getIsPublic()) {
            throw new NotSupportedInteractionTypeException("Cannot save private plan");
        }
        return UserPlan.createWithUserPlanType(user, entity, type);
    }

    @Override
    protected Optional<UserPlan> findUserEntityOptional(int userId, int entityId, TypeOfInteraction type) {
        return ((UserPlanRepository) userEntityRepository)
                .findByUserIdAndPlanIdAndType(userId, entityId, type);
    }
}

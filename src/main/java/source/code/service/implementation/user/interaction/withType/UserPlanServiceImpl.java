package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userPlanService")
public class UserPlanServiceImpl
        extends GenericSavedService<Plan, UserPlan, PlanResponseDto>
        implements SavedService {

    private final ImageUrlPopulationService imagePopulationService;

    public UserPlanServiceImpl(UserPlanRepository userPlanRepository,
                               PlanRepository planRepository,
                               UserRepository userRepository,
                               PlanMapper planMapper,
                               ImageUrlPopulationService imagePopulationService) {
        super(userRepository,
                planRepository,
                userPlanRepository,
                planMapper::toResponseDto,
                Plan.class);
        this.imagePopulationService = imagePopulationService;
    }

    @Override
    public Page<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
        return ((PlanRepository) entityRepository)
                .findPlanSummaryUnified(userId, type, true, null, pageable)
                .map(dto -> {
                    imagePopulationService.populateAuthorAndEntityImages(dto, PlanSummaryDto::getAuthorImageName,
                            PlanSummaryDto::setAuthorImageUrl, PlanSummaryDto::getFirstImageName,
                            PlanSummaryDto::setFirstImageUrl);
                    return dto;
                });
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
    protected UserPlan findUserEntity(int userId, int planId, TypeOfInteraction type) {
        return ((UserPlanRepository) userEntityRepository)
                .findByUserIdAndPlanIdAndType(userId, planId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserPlan.class,
                        userId,
                        planId,
                        type
                ));
    }

    @Override
    protected List<UserPlan> findAllByUserAndType(int userId, TypeOfInteraction type) {
        return ((UserPlanRepository) userEntityRepository).findByUserIdAndType(userId, type);
    }

    @Override
    protected Plan extractEntity(UserPlan userPlan) {
        return userPlan.getPlan();
    }

    @Override
    protected long countSaves(int planId) {
        return ((UserPlanRepository) userEntityRepository).countByPlanIdAndType(planId, TypeOfInteraction.SAVE);
    }

    @Override
    protected long countLikes(int planId) {
        return ((UserPlanRepository) userEntityRepository).countByPlanIdAndType(planId, TypeOfInteraction.LIKE);
    }

    private boolean isCurrentUser(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }}

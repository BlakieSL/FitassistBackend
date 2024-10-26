package source.code.service.Implementation.User;

import org.springframework.stereotype.Service;
import source.code.dto.Response.PlanResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Plan.PlanMapper;
import source.code.model.Plan.Plan;
import source.code.model.User.User;
import source.code.model.User.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.Declaration.User.SavedService;

import java.util.List;

@Service("userPlanService")
public class UserPlanServiceImpl
        extends GenericSavedService<Plan, UserPlan, PlanResponseDto>
        implements SavedService{

  public UserPlanServiceImpl(UserPlanRepository userPlanRepository,
                             PlanRepository planRepository,
                             UserRepository userRepository,
                             PlanMapper planMapper) {
    super(userRepository,
            planRepository,
            userPlanRepository,
            planMapper::toResponseDto,
            Plan.class);
  }

  @Override
  protected boolean isAlreadySaved(int userId, int planId, short type) {
    return ((UserPlanRepository) userEntityRepository)
            .existsByUserIdAndPlanIdAndType(userId, planId, type);
  }

  @Override
  protected UserPlan createUserEntity(User user, Plan entity, short type) {
    return UserPlan.createWithUserPlanType(user, entity, type);
  }

  @Override
  protected UserPlan findUserEntity(int userId, int planId, short type) {
    return ((UserPlanRepository) userEntityRepository)
            .findByUserIdAndPlanIdAndType(userId, planId, type)
            .orElseThrow(() -> new RecordNotFoundException(UserPlan.class, userId, planId, type));
  }

  @Override
  protected List<UserPlan> findAllByUserAndType(int userId, short type) {
    return ((UserPlanRepository) userEntityRepository).findByUserIdAndType(userId, type);
  }

  @Override
  protected Plan extractEntity(UserPlan userPlan) {
    return userPlan.getPlan();
  }

  @Override
  protected long countSaves(int planId) {
    return ((UserPlanRepository) userEntityRepository).countByPlanIdAndType(planId, (short) 1);
  }

  @Override
  protected long countLikes(int planId) {
    return ((UserPlanRepository) userEntityRepository).countByPlanIdAndType(planId, (short) 2);
  }
}

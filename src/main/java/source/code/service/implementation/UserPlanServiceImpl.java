package source.code.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Plan.Plan;
import source.code.model.User.User;
import source.code.model.User.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserPlanService;

import java.util.NoSuchElementException;

@Service
public class UserPlanServiceImpl implements UserPlanService {
  private final UserPlanRepository userPlanRepository;
  private final PlanRepository planRepository;
  private final UserRepository userRepository;

  public UserPlanServiceImpl(UserPlanRepository userPlanRepository,
                             PlanRepository planRepository,
                             UserRepository userRepository) {
    this.userPlanRepository = userPlanRepository;
    this.planRepository = planRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void savePlanToUser(int userId, int planId,  short type) {
    if (isAlreadySaved(userId, planId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has plan with id: " + planId
                      + " and type: " + type);
    }
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));

    Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + planId + " not found"));

    UserPlan userPlan =
            UserPlan.createWithUserPlanType(user, plan, type);
    userPlanRepository.save(userPlan);
  }

  private boolean isAlreadySaved(int userId, int planId, short type) {
    return userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type);
  }

  @Transactional
  public void deleteSavedPlanFromUser(int planId, int userId, short type) {
    UserPlan userPlan = userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type)
            .orElseThrow(() -> new NoSuchElementException(
                    "UserPlan with user id: " + userId
                            + ", plan id: " + planId
                            + " and type: " + type + " not found"));

    userPlanRepository.delete(userPlan);
  }

  public LikesAndSavesResponseDto calculatePlanLikesAndSaves(int planId) {
    planRepository.findById(planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + planId + " not found"));

    long saves = userPlanRepository.countByPlanIdAndType(planId, (short) 1);
    long likes = userPlanRepository.countByPlanIdAndType(planId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }
}
package source.code.service;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.helper.ValidationHelper;
import source.code.model.Plan;
import source.code.model.User;
import source.code.model.UserPlan;
import source.code.repository.PlanRepository;
import source.code.repository.UserPlanRepository;
import source.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserPlanService {
    private final ValidationHelper validationHelper;
    private final UserPlanRepository userPlanRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public UserPlanService(
            final ValidationHelper validationHelper,
            final UserPlanRepository userPlanRepository,
            final PlanRepository planRepository,
            final UserRepository userRepository) {
        this.validationHelper = validationHelper;
        this.userPlanRepository = userPlanRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void savePlanToUser(int planId, int userId, short type) {
        if(userPlanRepository.existsByUserIdAndPlanIdAndType(userId, planId, type)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId +
                            " already has plan with id: " + planId +
                            " and type: " + type);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Plan with id: " + planId + " not found"));

        UserPlan userPlan = new UserPlan();
        userPlan.setUser(user);
        userPlan.setPlan(plan);
        userPlanRepository.save(userPlan);
    }

    @Transactional
    public void deleteSavedPlanFromUser(int planId, int userId, short type) {
        UserPlan userPlan = userPlanRepository.findByUserIdAndPlanIdAndType(userId, planId, type)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserPlan with user id: " + userId +
                                ", plan id: " + planId +
                                " and type: " + type + " not found"));

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
package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Models.Plan;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Models.UserPlan;
import com.example.simplefullstackproject.Repositories.PlanRepository;
import com.example.simplefullstackproject.Repositories.UserPlanRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
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
    public void addPlanToUser(Integer planId, Integer userId) {
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
    public void deletePlanFromUser(Integer planId, Integer userId) {
        UserPlan userPlan = userPlanRepository.findByUserIdAndPlanId(userId, planId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserPlan with user id: " + userId +
                                " and plan id: " + planId + " not found"));

        userPlanRepository.delete(userPlan);
    }
}
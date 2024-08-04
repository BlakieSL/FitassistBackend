package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.exception.NotUniqueRecordException;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.model.Plan;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.model.UserPlan;
import com.example.simplefullstackproject.repository.PlanRepository;
import com.example.simplefullstackproject.repository.UserPlanRepository;
import com.example.simplefullstackproject.repository.UserRepository;
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
        if(userPlanRepository.existsByUserIdAndPlanId(userId,planId)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId + " already has plan with id: " + planId);
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
    public void deletePlanFromUser(Integer planId, Integer userId) {
        UserPlan userPlan = userPlanRepository.findByUserIdAndPlanId(userId, planId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserPlan with user id: " + userId +
                                " and plan id: " + planId + " not found"));

        userPlanRepository.delete(userPlan);
    }
}
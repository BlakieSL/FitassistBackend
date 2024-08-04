package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.dtos.PlanDto;
import com.example.simplefullstackproject.models.Plan;
import com.example.simplefullstackproject.models.UserPlan;
import com.example.simplefullstackproject.repositories.PlanRepository;
import com.example.simplefullstackproject.repositories.UserPlanRepository;
import com.example.simplefullstackproject.services.Mappers.PlanDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PlanService {
    private final ValidationHelper validationHelper;
    private final PlanDtoMapper planDtoMapper;
    private final PlanRepository planRepository;
    private final UserPlanRepository userPlanRepository;

    public PlanService(ValidationHelper validationHelper,
                       PlanDtoMapper planDtoMapper,
                       PlanRepository planRepository,
                       UserPlanRepository userPlanRepository) {
        this.validationHelper = validationHelper;
        this.planDtoMapper = planDtoMapper;
        this.planRepository = planRepository;
        this.userPlanRepository = userPlanRepository;
    }

    @Transactional
    public PlanDto savePlan(PlanDto planDto) {
        validationHelper.validate(planDto);

        Plan plan = planRepository.save(planDtoMapper.map(planDto));
        return planDtoMapper.map(plan);
    }

    public PlanDto getPlanById(Integer id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Plan with id: " + id + " not found"));
        return planDtoMapper.map(plan);
    }

    public List<PlanDto> getPlans() {
        List<Plan> plans = planRepository.findAll();
        return plans.stream()
                .map(planDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<PlanDto> getPlansByUserID(Integer userId) {
        List<UserPlan> userPlans = userPlanRepository.findByUserId(userId);
        List<Plan> plans = userPlans.stream()
                .map(UserPlan::getPlan)
                .collect(Collectors.toList());
        return plans.stream()
                .map(planDtoMapper::map)
                .collect(Collectors.toList());
    }
}
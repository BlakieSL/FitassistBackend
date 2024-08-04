package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.PlanDto;
import com.example.simplefullstackproject.helper.ValidationHelper;
import com.example.simplefullstackproject.mapper.PlanMapper;
import com.example.simplefullstackproject.model.Plan;
import com.example.simplefullstackproject.model.UserPlan;
import com.example.simplefullstackproject.repository.PlanRepository;
import com.example.simplefullstackproject.repository.UserPlanRepository;
import com.example.simplefullstackproject.service.Mappers.PlanDtoMapper;
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

    public PlanService(ValidationHelper validationHelper,
                       PlanMapper planMapper,
                       PlanRepository planRepository,
                       UserPlanRepository userPlanRepository) {
        this.validationHelper = validationHelper;
        this.planMapper = planMapper;
        this.planRepository = planRepository;
        this.userPlanRepository = userPlanRepository;
    }

    @Transactional
    public PlanDto savePlan(PlanDto planDto) {
        validationHelper.validate(planDto);
        Plan plan = planRepository.save(planMapper.toEntity(planDto));
        return planMapper.toDto(plan);
    }

    public PlanDto getPlanById(Integer id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Plan with id: " + id + " not found"));
        return planMapper.toDto(plan);
    }

    public List<PlanDto> getPlans() {
        List<Plan> plans = planRepository.findAll();
        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PlanDto> getPlansByUserID(Integer userId) {
        List<UserPlan> userPlans = userPlanRepository.findByUserId(userId);
        List<Plan> plans = userPlans.stream()
                .map(UserPlan::getPlan)
                .collect(Collectors.toList());
        return plans.stream()
                .map(planMapper::toDto)
                .collect(Collectors.toList());
    }
}
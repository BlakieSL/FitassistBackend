package com.fitassist.backend.service.implementation.user.interaction.withType;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.mapper.plan.PlanMapper;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.user.TypeOfInteraction;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserPlan;
import com.fitassist.backend.repository.PlanRepository;
import com.fitassist.backend.repository.UserPlanRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.plan.PlanPopulationService;
import com.fitassist.backend.service.declaration.user.SavedService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userPlanService")
public class UserPlanServiceImpl extends GenericSavedService<Plan, UserPlan, PlanResponseDto> implements SavedService {

	private final PlanMapper planMapper;

	private final PlanPopulationService planPopulationService;

	public UserPlanServiceImpl(UserPlanRepository userPlanRepository, PlanRepository planRepository,
			UserRepository userRepository, PlanMapper planMapper, PlanPopulationService planPopulationService) {
		super(userRepository, planRepository, userPlanRepository, Plan.class, UserPlan.class);
		this.planMapper = planMapper;
		this.planPopulationService = planPopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.PLANS, key = "#entityId")
	public InteractionResponseDto saveToUser(int entityId, TypeOfInteraction type) {
		return super.saveToUser(entityId, type);
	}

	@Override
	@CacheEvict(value = CacheNames.PLANS, key = "#entityId")
	public InteractionResponseDto deleteFromUser(int entityId, TypeOfInteraction type) {
		return super.deleteFromUser(entityId, type);
	}

	@Override
	protected long countByEntityIdAndType(int entityId, TypeOfInteraction type) {
		return ((UserPlanRepository) userEntityRepository).countByPlanIdAndType(entityId, type);
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
		Page<UserPlan> userPlanPage = ((UserPlanRepository) userEntityRepository).findAllByUserIdAndType(userId, type,
				pageable);

		List<Integer> planIds = userPlanPage.getContent().stream().map(up -> up.getPlan().getId()).toList();

		List<Plan> plansWithDetails = ((PlanRepository) entityRepository).findByIdsWithDetails(planIds);

		Map<Integer, Plan> planMap = plansWithDetails.stream().collect(Collectors.toMap(Plan::getId, p -> p));

		List<PlanSummaryDto> summaries = userPlanPage.getContent().stream().map(up -> {
			Plan plan = planMap.get(up.getPlan().getId());
			PlanSummaryDto dto = planMapper.toSummary(plan);
			dto.setInteractionCreatedAt(up.getCreatedAt());
			return dto;
		}).toList();

		planPopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userPlanPage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int planId, TypeOfInteraction type) {
		return ((UserPlanRepository) userEntityRepository).existsByUserIdAndPlanIdAndType(userId, planId, type);
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
		return ((UserPlanRepository) userEntityRepository).findByUserIdAndPlanIdAndType(userId, entityId, type);
	}

}

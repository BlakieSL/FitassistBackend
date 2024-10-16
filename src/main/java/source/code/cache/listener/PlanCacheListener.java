package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Plan.Plan;
import source.code.dto.request.PlanCreateDto;

@Component
public class PlanCacheListener {
  private final CacheManager cacheManager;

  public PlanCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handlePlanCreate(Plan.PlanCreateEvent event) {
    PlanCreateDto dto = event.getPlanCreateDto();

    cacheManager.getCache("allPlans").clear();

    cacheManager.getCache("TYPE_" + dto.getPlanTypeId());
    cacheManager.getCache("DURATION_" + dto.getPlanDurationId());
    cacheManager.getCache("EQUIPMENT_" + dto.getPlanEquipmentId());
    cacheManager.getCache("EXPERTISE_LEVEL_" + dto.getPlanExpertiseLevelId());

    if (dto.getCategoryIds() != null) {
      for (int categoryId : dto.getCategoryIds()) {
        cacheManager.getCache("plansByCategory").evict(categoryId);
      }
    }
  }
}

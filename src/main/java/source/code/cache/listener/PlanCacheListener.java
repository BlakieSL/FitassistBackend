package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.Plan.Plan;
import source.code.cache.event.Plan.PlanCreateEvent;
import source.code.cache.event.Plan.PlanDeleteEvent;
import source.code.cache.event.Plan.PlanUpdateEvent;
import source.code.dto.request.PlanCreateDto;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;

@Component
public class PlanCacheListener {
  private final CacheManager cacheManager;

  public PlanCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handlePlanCreate(PlanCreateEvent event) {
    Plan plan = event.getPlan();
    clearCommonCache(plan);
  }

  @EventListener
  public void handlePlanUpdate(PlanUpdateEvent event) {
    Plan plan = event.getPlan();
    clearCache(plan);
  }

  @EventListener
  public void handlePlanDelete(PlanDeleteEvent event) {
    Plan plan = event.getPlan();
    clearCache(plan);
  }

  private void clearCache(Plan plan) {
    clearCommonCache(plan);
    cacheManager.getCache("plans").evict(plan.getId());
  }

  private void clearCommonCache(Plan plan) {
    cacheManager.getCache("allPlans").clear();
    clearPlansByFieldCache(plan);
    clearPlansByCategoryCache(plan);
  }


  private void clearPlansByFieldCache(Plan plan) {
    cacheManager.getCache("TYPE_" + plan.getPlanType().getId());
    cacheManager.getCache("DURATION_" + plan.getPlanDuration().getId());
    cacheManager.getCache("EQUIPMENT_" + plan.getPlanEquipment().getId());
    cacheManager.getCache("EXPERTISE_LEVEL_" + plan.getPlanExpertiseLevel().getId());
  }

  private void clearPlansByCategoryCache(Plan plan) {
    if(plan.getPlanCategoryAssociations() != null) {
      for(PlanCategoryAssociation association : plan.getPlanCategoryAssociations()) {
        cacheManager.getCache("plansByCategory").evict(association.getPlanCategory().getId());
      }
    }
  }
}

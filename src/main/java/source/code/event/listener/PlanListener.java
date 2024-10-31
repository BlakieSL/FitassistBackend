package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Plan.PlanCreateEvent;
import source.code.event.events.Plan.PlanDeleteEvent;
import source.code.event.events.Plan.PlanUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.helper.Enum.PlanField;
import source.code.model.Plan.Plan;
import source.code.model.Plan.PlanCategoryAssociation;
import source.code.service.Declaration.Cache.CacheService;
import source.code.service.Declaration.Search.LuceneIndexService;

@Component
public class PlanListener {
  private final CacheService cacheService;
  private final LuceneIndexService luceneService;

  public PlanListener(CacheService cacheService, LuceneIndexService luceneService) {
    this.cacheService = cacheService;
    this.luceneService = luceneService;
  }

  @EventListener
  public void handlePlanCreate(PlanCreateEvent event) {
    Plan plan = event.getPlan();

    clearCommonCache(plan);
    luceneService.addEntity(plan);
  }

  @EventListener
  public void handlePlanUpdate(PlanUpdateEvent event) {
    Plan plan = event.getPlan();

    clearCache(plan);
    luceneService.updateEntity(plan);
  }

  @EventListener
  public void handlePlanDelete(PlanDeleteEvent event) {
    Plan plan = event.getPlan();

    clearCache(plan);
    luceneService.deleteEntity(plan);
  }

  private void clearCache(Plan plan) {
    cacheService.evictCache(CacheNames.PLANS, plan.getId());
    clearCommonCache(plan);
  }

  private void clearCommonCache(Plan plan) {
    cacheService.clearCache(CacheNames.ALL_PLANS);
    clearPlansByFieldCache(plan);
    clearPlansByCategoryCache(plan);
  }

  private void clearPlansByFieldCache(Plan plan) {
    cacheService.evictCache(CacheNames.PLANS_BY_FIELD,
            PlanField.TYPE.toString() + plan.getPlanType().getId());

    cacheService.evictCache(CacheNames.PLANS_BY_FIELD,
            PlanField.DURATION.toString() + plan.getPlanDuration().getId());

    cacheService.evictCache(CacheNames.PLANS_BY_FIELD,
            PlanField.EXPERTISE_LEVEL.toString() + plan.getExpertiseLevel().getId());
  }

  private void clearPlansByCategoryCache(Plan plan) {
    if (plan.getPlanCategoryAssociations() != null) {
      for (PlanCategoryAssociation association : plan.getPlanCategoryAssociations()) {
        cacheService.evictCache(CacheNames.PLANS_BY_CATEGORY,
                association.getPlanCategory().getId());
      }
    }
  }
}

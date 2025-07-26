package source.code.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.Plan.PlanCreateEvent;
import source.code.event.events.Plan.PlanDeleteEvent;
import source.code.event.events.Plan.PlanUpdateEvent;
import source.code.helper.Enum.cache.CacheNames;
import source.code.model.plan.Plan;
import source.code.service.declaration.cache.CacheService;
import source.code.service.declaration.search.LuceneIndexService;

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
    }
}

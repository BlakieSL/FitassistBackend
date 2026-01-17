package com.fitassist.backend.event.listener;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.event.events.Plan.PlanCreateEvent;
import com.fitassist.backend.event.events.Plan.PlanDeleteEvent;
import com.fitassist.backend.event.events.Plan.PlanUpdateEvent;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.service.declaration.cache.CacheService;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

		if (plan.getIsPublic()) {
			luceneService.addEntity(plan);
		}
	}

	@EventListener
	public void handlePlanUpdate(PlanUpdateEvent event) {
		Plan plan = event.getPlan();

		clearCache(plan);
		if (plan.getIsPublic()) {
			luceneService.updateEntity(plan);
		}
	}

	@EventListener
	public void handlePlanDelete(PlanDeleteEvent event) {
		Plan plan = event.getPlan();

		clearCache(plan);
		luceneService.deleteEntity(plan);
	}

	private void clearCache(Plan plan) {
		cacheService.evictCache(CacheNames.PLANS, plan.getId());
	}

}

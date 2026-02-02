package com.fitassist.backend.mapper.context;

import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlanMappingContext {

	private User user;

	private List<PlanCategory> categories;

	public static PlanMappingContext forCreate(User user, List<PlanCategory> categories) {
		return new PlanMappingContext(user, categories);
	}

	public static PlanMappingContext forUpdate(List<PlanCategory> categories) {
		return new PlanMappingContext(null, categories);
	}

}

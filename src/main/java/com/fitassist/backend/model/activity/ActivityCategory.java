package com.fitassist.backend.model.activity;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activity_category")
@Getter
@Setter
public class ActivityCategory extends CategoryBase {

	@OneToMany(mappedBy = "activityCategory")
	private final Set<Activity> activities = new HashSet<>();

}

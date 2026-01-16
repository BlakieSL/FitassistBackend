package com.fitassist.backend.model.activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a category of activity. Known predefined values (subject to change):
 *
 * <ul>
 * <li>{@code HOME}
 * <li>{@code LAWN_OR_GARDEN}
 * <li>{@code WALKING}
 * <li>{@code TRANSPORTATION}
 * <li>{@code OCCUPATION}
 * <li>{@code SPORTS}
 * <li>{@code WATER}
 * <li>{@code WINTER}
 * <li>{@code RELIGIOUS}
 * </ul>
 *
 * <p>
 * These values can be extended or modified via the application or admin interface.
 */
@Entity
@Table(name = "activity_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCategory {

	private static final int NAME_MAX_LENGTH = 50;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@OneToMany(mappedBy = "activityCategory")
	private final Set<Activity> activities = new HashSet<>();

}

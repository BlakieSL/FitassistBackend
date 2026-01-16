package com.fitassist.backend.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the type of force applied during an exercise movement.
 *
 * <p>
 * <strong>Known predefined values:</strong>
 *
 * <ul>
 * <li>{@code PUSH} - Movement where force is applied away from the body (e.g., push-ups).
 * <li>{@code PULL} - Movement where force is applied toward the body (e.g., pull-ups).
 * <li>{@code ROTATION} - Movement involving torso or limb rotation (e.g., Russian
 * twists).
 * <li>{@code HOLD} - Static position requiring muscular tension without movement (e.g.,
 * plank).
 * <li>{@code CARRY} - Movement that involves holding weight while moving (e.g., farmer's
 * carry).
 * </ul>
 *
 * <p>
 * Note: These values are not hardcoded and may be extended or modified through the
 * application.
 */
@Entity
@Table(name = "force_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForceType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "forceType", cascade = CascadeType.REMOVE)
	private final Set<Exercise> exercises = new HashSet<>();

}

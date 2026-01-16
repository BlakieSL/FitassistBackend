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
 * Represents a type of fitness equipment used during exercise.
 *
 * <p>
 * <strong>Known predefined values:</strong>
 *
 * <ul>
 * <li>{@code DUMBBELL}
 * <li>{@code BARBELL}
 * <li>{@code KETTLEBELL}
 * <li>{@code RESISTANCE_BAND}
 * <li>{@code CABLE_MACHINE}
 * <li>{@code SMITH_MACHINE}
 * <li>{@code BODYWEIGHT}
 * <li>{@code BENCH}
 * <li>{@code PULL_UP_BAR}
 * <li>{@code SQUAT_RACK}
 * </ul>
 *
 * <p>
 * Note: These values are not hardcoded and may be extended or modified through the
 * application.
 */
@Entity
@Table(name = "equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "equipment", cascade = CascadeType.REMOVE)
	private final Set<Exercise> exercises = new HashSet<>();

}

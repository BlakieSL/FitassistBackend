package com.fitassist.backend.model.exercise;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "target_muscle")
@Getter
@Setter
public class TargetMuscle extends CategoryBase {

	@OneToMany(mappedBy = "targetMuscle")
	private final Set<ExerciseTargetMuscle> exerciseTargetMuscles = new HashSet<>();

}

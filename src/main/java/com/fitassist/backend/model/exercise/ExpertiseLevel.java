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
@Table(name = "expertise_level")
@Getter
@Setter
public class ExpertiseLevel extends CategoryBase {

	@OneToMany(mappedBy = "expertiseLevel")
	private final Set<Exercise> exercises = new HashSet<>();

}

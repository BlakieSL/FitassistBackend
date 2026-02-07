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
@Table(name = "equipment")
@Getter
@Setter
public class Equipment extends CategoryBase {

	@OneToMany(mappedBy = "equipment")
	private final Set<Exercise> exercises = new HashSet<>();

}

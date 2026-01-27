package com.fitassist.backend.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static com.fitassist.backend.model.SchemaConstants.CATEGORY_NAME_MAX_LENGTH;

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
	@Size(max = CATEGORY_NAME_MAX_LENGTH)
	@Column(nullable = false, length = CATEGORY_NAME_MAX_LENGTH)
	private String name;

	@OneToMany(mappedBy = "forceType", cascade = CascadeType.REMOVE)
	private final Set<Exercise> exercises = new HashSet<>();

}

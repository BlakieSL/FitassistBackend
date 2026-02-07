package com.fitassist.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.fitassist.backend.model.SchemaConstants.CATEGORY_NAME_MAX_LENGTH;

@MappedSuperclass
@Getter
@Setter
public abstract class CategoryBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = CATEGORY_NAME_MAX_LENGTH)
	@Column(nullable = false, length = CATEGORY_NAME_MAX_LENGTH)
	private String name;

}

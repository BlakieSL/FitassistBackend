package com.fitassist.backend.model.thread;

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
@Table(name = "thread_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThreadCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = CATEGORY_NAME_MAX_LENGTH)
	@Column(nullable = false, length = CATEGORY_NAME_MAX_LENGTH)
	private String name;

	@OneToMany(mappedBy = "threadCategory", cascade = CascadeType.REMOVE)
	private final Set<ForumThread> threads = new HashSet<>();

}

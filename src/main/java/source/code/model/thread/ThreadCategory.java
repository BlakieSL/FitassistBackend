package source.code.model.thread;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "threadCategory", cascade = CascadeType.REMOVE)
	private final Set<ForumThread> threads = new HashSet<>();

}

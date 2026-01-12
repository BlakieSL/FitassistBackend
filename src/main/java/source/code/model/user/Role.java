package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

	private static final int MIN_ROLE_NAME_LENGTH = 4;

	private static final int MAX_ROLE_NAME_LENGTH = 9;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(min = MIN_ROLE_NAME_LENGTH, max = MAX_ROLE_NAME_LENGTH)
	@Column(nullable = false, length = MAX_ROLE_NAME_LENGTH, unique = true)
	private String name;

	@ManyToMany(mappedBy = "roles")
	private final Set<User> users = new HashSet<>();

}

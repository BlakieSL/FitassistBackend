package source.code.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	@EntityGraph(attributePaths = {"roles"})
	Optional<User> findUserWithRolesByEmail(String email);

}

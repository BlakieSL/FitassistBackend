package source.code.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Optional<Role> findByName(String name);

}

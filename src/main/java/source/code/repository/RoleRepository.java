package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.helper.Enum.model.RoleEnum;
import source.code.model.user.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Optional<Role> findByName(RoleEnum name);

}

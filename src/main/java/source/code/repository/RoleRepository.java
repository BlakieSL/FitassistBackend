package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(String name);
}
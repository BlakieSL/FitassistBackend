package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

}
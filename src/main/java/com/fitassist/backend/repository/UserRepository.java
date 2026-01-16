package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	@EntityGraph(attributePaths = { "roles" })
	Optional<User> findUserWithRolesByEmail(String email);

}

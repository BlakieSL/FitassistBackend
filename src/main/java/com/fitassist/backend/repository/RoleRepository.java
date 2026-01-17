package com.fitassist.backend.repository;

import com.fitassist.backend.model.user.Role;
import com.fitassist.backend.model.user.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Optional<Role> findByName(RoleEnum name);

}

package com.fitassist.backend.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.fitassist.backend.dto.pojo.UserCredentialsDto;

public class UserDetailsHelper {

	public static UserDetails buildUserDetails(UserCredentialsDto dto) {
		return User.builder()
			.username(dto.getEmail())
			.password(dto.getPassword())
			.roles(dto.getRoles().toArray(String[]::new))
			.build();
	}

}

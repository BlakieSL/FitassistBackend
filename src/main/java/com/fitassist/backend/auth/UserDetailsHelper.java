package com.fitassist.backend.auth;

import com.fitassist.backend.dto.pojo.UserCredentialsDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsHelper {

	public static UserDetails buildUserDetails(UserCredentialsDto dto) {
		return User.builder()
			.username(dto.getEmail())
			.password(dto.getPassword())
			.roles(dto.getRoles().toArray(String[]::new))
			.build();
	}

}

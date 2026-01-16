package com.fitassist.backend.integration.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fitassist.backend.auth.CustomAuthenticationToken;

import java.util.List;

public class Utils {

	public static void setUserContext(int userId) {
		setContext(userId, "ROLE_USER");
	}

	public static void setAdminContext(int userId) {
		setContext(userId, "ROLE_ADMIN");
	}

	private static void setContext(int userId, String role) {
		var auth = new CustomAuthenticationToken("admin", userId, null, List.of(new SimpleGrantedAuthority(role)));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}

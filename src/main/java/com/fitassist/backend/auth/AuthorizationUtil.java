package com.fitassist.backend.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationUtil {

	public static int getUserId() {
		return getAuthToken().map(CustomAuthenticationToken::getUserId).orElse(-1);
	}

	private static Optional<CustomAuthenticationToken> getAuthToken() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof CustomAuthenticationToken token) {
			return Optional.of(token);
		}
		return Optional.empty();
	}

	public static boolean isAdmin() {
		return hasRole("ROLE_ADMIN");
	}

	public static boolean isModerator() {
		return hasRole("ROLE_MODERATOR");
	}

	private static boolean hasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
	}

	public static boolean isAdminOrModerator() {
		return isAdmin() || isModerator();
	}

	public static boolean isOwnerOrAdmin(Integer ownerId) {
		return isAdmin() || isOwner(ownerId);
	}

	public static boolean isOwnerOrAdminOrModerator(Integer ownerId) {
		return isAdminOrModerator() || isOwner(ownerId);
	}

	private static boolean isOwner(Integer ownerId) {
		return ownerId != null && ownerId.equals(getUserId());
	}

}

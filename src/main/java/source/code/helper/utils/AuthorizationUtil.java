package source.code.helper.utils;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import source.code.auth.CustomAuthenticationToken;

@Service
public class AuthorizationUtil {

	public static boolean isOwnerOrAdmin(Integer ownerId) {
		if (isAdmin()) {
			return true;
		}

		Integer currentUserId = getUserId();
		return Optional.ofNullable(ownerId).map(id -> id.equals(currentUserId)).orElse(false);
	}

	public static int getUserId() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof CustomAuthenticationToken) {
			return ((CustomAuthenticationToken) auth).getUserId();
		}
		return -1;
	}

	public static boolean isAdmin() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof CustomAuthenticationToken) {
			return ((CustomAuthenticationToken) auth).getAuthorities()
				.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		}
		return false;
	}

	public static boolean isModerator() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof CustomAuthenticationToken) {
			return ((CustomAuthenticationToken) auth).getAuthorities()
				.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MODERATOR"));
		}
		return false;
	}

	public static boolean isAdminOrModerator() {
		return isAdmin() || isModerator();
	}

	public static boolean isOwnerOrAdminOrModerator(Integer ownerId) {
		if (isAdminOrModerator()) {
			return true;
		}

		Integer currentUserId = getUserId();
		return Optional.ofNullable(ownerId).map(id -> id.equals(currentUserId)).orElse(false);
	}

}

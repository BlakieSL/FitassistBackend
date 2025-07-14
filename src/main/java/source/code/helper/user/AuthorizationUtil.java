package source.code.helper.user;

import org.springframework.security.core.context.SecurityContextHolder;
import source.code.auth.CustomAuthenticationToken;

import java.util.Optional;

public class AuthorizationUtil {
    public static boolean isOwnerOrAdmin(Integer ownerId) {
        CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        Integer currentUserId = auth.getUserId();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || Optional.ofNullable(ownerId)
                .map(id -> id.equals(currentUserId))
                .orElse(false);
    }


    public static int getUserId() {
        CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getUserId();
    }
}

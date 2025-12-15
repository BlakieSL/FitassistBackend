package source.code.helper.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import source.code.auth.CustomAuthenticationToken;

import java.util.Optional;

@Service
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
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken) {
            return ((CustomAuthenticationToken) auth).getUserId();
        }
        return -1;
    }
}

package source.code.helper.User;

import org.springframework.security.core.context.SecurityContextHolder;
import source.code.auth.CustomAuthenticationToken;
import source.code.model.forum.Comment;
import source.code.model.forum.ForumThread;

public class AuthorizationUtil {
    public static boolean isOwnerOrAdmin(int ownerId) {
        CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        Integer currentUserId = auth.getUserId();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || ownerId == currentUserId;
    }

    public static int getUserId() {
        CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getUserId();
    }
}

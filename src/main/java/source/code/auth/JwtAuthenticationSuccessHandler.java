package source.code.auth;

import source.code.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationSuccessHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private record JwtWrapper(String accessToken, String refreshToken) {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        Integer userId = userService.getUserIdByEmail(authentication.getName());


        String accessToken = jwtService.createAccessToken(authentication.getName(), userId, authorities);
        String refreshToken = jwtService.createRefreshToken(authentication.getName(), userId, authorities);
        new ObjectMapper().writeValue(response.getWriter(), new JwtWrapper(accessToken, refreshToken));
    }
}

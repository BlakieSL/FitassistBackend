package source.code.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import source.code.service.implementation.user.UserServiceImpl;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserServiceImpl userServiceImpl;

    public JwtAuthenticationSuccessHandler(JwtService jwtService, UserServiceImpl userServiceImpl) {
        this.jwtService = jwtService;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Integer userId = userServiceImpl.getUserIdByEmail(authentication.getName());

        String accessToken = jwtService.createAccessToken(authentication.getName(), userId, authorities);
        String refreshToken = jwtService.createRefreshToken(authentication.getName(), userId, authorities);
        new ObjectMapper().writeValue(response.getWriter(), new JwtWrapper(accessToken, refreshToken));
    }

    private record JwtWrapper(String accessToken, String refreshToken) {
    }
}

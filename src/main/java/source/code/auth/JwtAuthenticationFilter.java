package source.code.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import source.code.service.implementation.user.UserServiceImpl;

public class JwtAuthenticationFilter extends HttpFilter {

	private static final RequestMatcher defaultRequestMatcher = (
			request) -> "/api/users/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod());

	private final AuthenticationManager authenticationManager;

	private final AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

	private final AuthenticationSuccessHandler successHandler;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService,
			UserServiceImpl userServiceImpl) {
		this.authenticationManager = authenticationManager;
		successHandler = new JwtAuthenticationSuccessHandler(jwtService, userServiceImpl);
	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!defaultRequestMatcher.matches(request)) {
			chain.doFilter(request, response);
		}
		else {
			try {
				JwtAuthenticationToken jwtAuthentication = new ObjectMapper().readValue(request.getInputStream(),
						JwtAuthenticationToken.class);

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						jwtAuthentication.username(), jwtAuthentication.password());

				Authentication authenticationResult = authenticationManager
					.authenticate(usernamePasswordAuthenticationToken);

				successHandler.onAuthenticationSuccess(request, response, authenticationResult);
			}
			catch (AuthenticationException ex) {
				failureHandler.onAuthenticationFailure(request, response, ex);
			}
		}
	}

	private record JwtAuthenticationToken(String username, String password) {
	}

}

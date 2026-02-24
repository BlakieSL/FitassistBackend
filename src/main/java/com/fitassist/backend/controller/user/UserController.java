package com.fitassist.backend.controller.user;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.annotation.AccountOwnerOrAdmin;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.auth.CookieService;
import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.exception.InvalidRefreshTokenException;
import com.fitassist.backend.service.declaration.user.UserService;
import jakarta.json.JsonMergePatch;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

	private final UserService userService;

	private final JwtService jwtService;

	private final CookieService cookieService;

	public UserController(UserService userService, JwtService jwtService, CookieService cookieService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.cookieService = cookieService;
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponseDto> getCurrentUser() {
		int userId = AuthorizationUtil.getUserId();
		UserResponseDto user = userService.getUser(userId);
		return ResponseEntity.ok(user);
	}

	@AccountOwnerOrAdmin
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
		UserResponseDto user = userService.getUser(id);
		return ResponseEntity.ok(user);
	}

	@GetMapping("/public/{id}")
	public ResponseEntity<AuthorDto> getPublicUser(@PathVariable int id) {
		AuthorDto authorDto = userService.getPublicUser(id);
		return ResponseEntity.ok(authorDto);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = cookieService.getRefreshTokenFromCookie(request)
			.orElseThrow(() -> new InvalidRefreshTokenException("No refresh token"));
		String newAccessToken = jwtService.refreshAccessToken(refreshToken);
		cookieService.setAccessTokenCookie(response, newAccessToken);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		cookieService.clearAuthCookies(response);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserCreateDto request) {
		UserResponseDto response = userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// deprecated - use put instead
	@AccountOwnerOrAdmin
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody JsonMergePatch patch)
			throws JacksonException {
		userService.updateUser(id, patch);
		return ResponseEntity.noContent().build();
	}

	@AccountOwnerOrAdmin
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateUserSimple(@PathVariable int id, @Valid @RequestBody UserUpdateDto updateDto) {
		userService.updateUserSimple(id, updateDto);
		return ResponseEntity.noContent().build();
	}

	@AccountOwnerOrAdmin
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

}

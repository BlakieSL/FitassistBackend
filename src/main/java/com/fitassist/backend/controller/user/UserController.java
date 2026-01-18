package com.fitassist.backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.annotation.AccountOwnerOrAdmin;
import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.request.auth.RefreshTokenRequestDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.other.AccessTokenResponseDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.service.declaration.user.UserService;
import com.fitassist.backend.validation.ValidationGroups;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

	private final UserService userService;

	private final JwtService jwtService;

	public UserController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
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
	public ResponseEntity<AccessTokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dtoRequest) {
		String newAccessToken = jwtService.refreshAccessToken(dtoRequest.getRefreshToken());
		AccessTokenResponseDto accessTokenResponseDto = AccessTokenResponseDto.of(newAccessToken);
		return ResponseEntity.ok(accessTokenResponseDto);
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserCreateDto request) {
		UserResponseDto response = userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	//deprecated - use put instead
	@AccountOwnerOrAdmin
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
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

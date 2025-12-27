package source.code.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.AccountOwnerOrAdmin;
import source.code.auth.JwtService;
import source.code.dto.request.auth.RefreshTokenRequestDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.request.user.UserUpdateDto;
import source.code.dto.response.AccessTokenResponseDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.service.declaration.user.UserService;
import source.code.validation.ValidationGroups;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

	private final UserService userService;

	private final JwtService jwtService;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

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

	@AccountOwnerOrAdmin
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable int id,
										   @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch)
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

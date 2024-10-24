package source.code.controller.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import source.code.auth.JwtService;
import source.code.dto.request.RefreshTokenRequestDto;
import source.code.dto.request.UserCreateDto;
import source.code.dto.response.AccessTokenResponseDto;
import source.code.dto.response.User.UserResponseDto;
import source.code.service.declaration.User.UserService;
import source.code.validation.ValidationGroups;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
  private final UserService userService;
  private final JwtService jwtService;

  public UserController(
          UserService userService,
          JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
    UserResponseDto user = userService.getUser(id);
    return ResponseEntity.ok(user);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dtoRequest) {
    String newAccessToken = jwtService.refreshAccessToken(dtoRequest.getRefreshToken());
    AccessTokenResponseDto accessTokenResponseDto = new AccessTokenResponseDto(newAccessToken);
    return ResponseEntity.ok(accessTokenResponseDto);
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserCreateDto request) {
    UserResponseDto response = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateUser(
          @PathVariable int id,
          @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    userService.updateUser(id, patch);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable int id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}

package source.code.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.IsOwnerOrAdmin;
import source.code.auth.JwtService;
import source.code.dto.request.RefreshTokenRequestDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.response.AccessTokenResponseDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.service.declaration.user.UserService;
import source.code.validation.ValidationGroups;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    @IsOwnerOrAdmin
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
        UserResponseDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dtoRequest) {
        String newAccessToken = jwtService.refreshAccessToken(dtoRequest.getRefreshToken());
        AccessTokenResponseDto accessTokenResponseDto = AccessTokenResponseDto.of(newAccessToken);
        return ResponseEntity.ok(accessTokenResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserCreateDto request) {
        UserResponseDto response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @IsOwnerOrAdmin
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable int id,
            @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        userService.updateUser(id, patch);
        return ResponseEntity.noContent().build();
    }

    @IsOwnerOrAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

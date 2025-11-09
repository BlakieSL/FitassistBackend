package source.code.controller.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.request.auth.PasswordResetDto;
import source.code.dto.request.auth.PasswordResetRequestDto;
import source.code.service.declaration.auth.PasswordResetService;

@RestController
@RequestMapping(path = "/api/password-reset")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto resetDto) {
        passwordResetService.resetPassword(resetDto);
        return ResponseEntity.ok().build();
    }
}
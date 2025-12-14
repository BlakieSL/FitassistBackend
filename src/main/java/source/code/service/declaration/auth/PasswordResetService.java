package source.code.service.declaration.auth;

import source.code.dto.request.auth.PasswordResetDto;
import source.code.dto.request.auth.PasswordResetRequestDto;

public interface PasswordResetService {
    void requestPasswordReset(PasswordResetRequestDto request);

    void resetPassword(PasswordResetDto resetDto);
}
package com.fitassist.backend.service.declaration.auth;

import com.fitassist.backend.dto.request.auth.PasswordResetDto;
import com.fitassist.backend.dto.request.auth.PasswordResetRequestDto;

public interface PasswordResetService {

	void requestPasswordReset(PasswordResetRequestDto request);

	void resetPassword(PasswordResetDto resetDto);

}

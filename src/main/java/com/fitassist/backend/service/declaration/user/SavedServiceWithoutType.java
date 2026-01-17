package com.fitassist.backend.service.declaration.user;

import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedServiceWithoutType {

	void saveToUser(int entityId);

	void deleteFromUser(int entityId);

	Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable);

}

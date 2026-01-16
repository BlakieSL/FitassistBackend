package com.fitassist.backend.service.declaration.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;

public interface SavedServiceWithoutType {

	void saveToUser(int entityId);

	void deleteFromUser(int entityId);

	Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable);

}

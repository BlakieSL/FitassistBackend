package com.fitassist.backend.service.declaration.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.model.user.TypeOfInteraction;

public interface SavedService {

	void saveToUser(int entityId, TypeOfInteraction type);

	void deleteFromUser(int entityId, TypeOfInteraction type);

	Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable);

}

package com.fitassist.backend.service.declaration.user;

import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.model.user.interactions.TypeOfInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedService {

	InteractionResponseDto saveToUser(int entityId, TypeOfInteraction type);

	InteractionResponseDto deleteFromUser(int entityId, TypeOfInteraction type);

	Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable);

}

package source.code.service.declaration.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;
import source.code.model.user.TypeOfInteraction;

public interface SavedService {
    void saveToUser(int entityId, TypeOfInteraction type);
    void deleteFromUser(int entityId, TypeOfInteraction type);
    Page<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable);
}

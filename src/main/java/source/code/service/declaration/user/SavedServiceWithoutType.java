package source.code.service.declaration.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;

public interface SavedServiceWithoutType {
    void saveToUser(int entityId);
    void deleteFromUser(int entityId);
    Page<BaseUserEntity> getAllFromUser(int userId, Pageable pageable);
}

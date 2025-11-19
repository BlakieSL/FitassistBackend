package source.code.service.declaration.user;

import org.springframework.data.domain.Sort;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;

import java.util.List;

public interface SavedServiceWithoutType {
    void saveToUser(int entityId);
    void deleteFromUser(int entityId);
    List<BaseUserEntity> getAllFromUser(int userId, Sort.Direction sortDirection);
    LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

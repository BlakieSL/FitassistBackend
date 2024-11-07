package source.code.service.declaration.user;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.Enum.Model.LikesAndSaves;
import source.code.model.user.BaseUserEntity;

import java.util.List;

public interface SavedServiceWithoutType {
    void saveToUser(int userId, int entityId);
    void deleteFromUser(int userId, int entityId);
    List<BaseUserEntity> getAllFromUser(int userId);
    LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

package source.code.service.declaration.user;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;

import java.util.List;

public interface SavedService {
    void saveToUser(int entityId, short type);

    void deleteFromUser(int entityId, short type);

    List<BaseUserEntity> getAllFromUser(short type);

    LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

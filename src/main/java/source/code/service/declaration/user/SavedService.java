package source.code.service.declaration.user;

import org.springframework.data.domain.Sort;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;
import source.code.model.user.TypeOfInteraction;

import java.util.List;

public interface SavedService {
    void saveToUser(int entityId, TypeOfInteraction type);

    void deleteFromUser(int entityId, TypeOfInteraction type);

    List<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Sort.Direction sortDirection);

    LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

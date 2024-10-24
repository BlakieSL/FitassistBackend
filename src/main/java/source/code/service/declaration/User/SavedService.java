package source.code.service.declaration.User;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.model.User.BaseUserEntity;

import java.util.List;

public interface SavedService {
  void saveToUser(int userId, int entityId, short type);
  void deleteFromUser(int userId, int entityId, short type);
  List<BaseUserEntity> getAllFromUser(int userId, short type);
  LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

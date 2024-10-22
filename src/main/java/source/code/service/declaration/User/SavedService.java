package source.code.service.declaration.User;

import source.code.dto.response.LikesAndSavesResponseDto;

import java.util.List;

public interface SavedService<R> {
  void saveToUser(int userId, int entityId, short type);
  void deleteFromUser(int userId, int entityId, short type);
  List<R> getAllFromUser(int userId, short type);
  LikesAndSavesResponseDto calculateLikesAndSaves(int entityId);
}

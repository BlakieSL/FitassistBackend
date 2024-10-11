package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;

public interface UserFoodService {
  void saveFoodToUser(int foodId, int userId, short type);

  void deleteSavedFoodFromUser(int foodId, int userId, short type);

  LikesAndSavesResponseDto calculateFoodLikesAndSaves(int foodId);
}

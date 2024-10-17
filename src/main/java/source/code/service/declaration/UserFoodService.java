package source.code.service.declaration;

import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;

import java.util.List;

public interface UserFoodService {
  void saveFoodToUser(int foodId, int userId, short type);

  void deleteSavedFoodFromUser(int foodId, int userId, short type);

  List<FoodResponseDto> getFoodsByUserAndType(int userId, short type);

  LikesAndSavesResponseDto calculateFoodLikesAndSaves(int foodId);
}

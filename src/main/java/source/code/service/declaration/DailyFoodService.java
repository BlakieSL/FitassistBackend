package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.model.DailyFood;

public interface DailyFoodService {
  void resetDailyCarts();

  void addFoodToDailyFoodItem(int userId, int foodId, DailyFoodItemCreateDto dto);

  void removeFoodFromDailyFoodItem(int userId, int foodId);

  void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  DailyFood createNewDailyFoodForUser(int userId);
  DailyFoodsResponseDto getFoodsFromDailyFoodItem(int userId);
}

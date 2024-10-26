package source.code.service.Declaration.Daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Food.DailyFoodItemCreateDto;
import source.code.dto.Response.DailyFoodsResponseDto;

public interface DailyFoodService {
  void addFoodToDailyFoodItem(int userId, int foodId, DailyFoodItemCreateDto dto);

  void removeFoodFromDailyFoodItem(int userId, int foodId);

  void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  DailyFoodsResponseDto getFoodsFromDailyFoodItem(int userId);
}

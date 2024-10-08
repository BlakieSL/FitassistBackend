package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.DailyCartFoodCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;

public interface DailyFoodService {
    void updateDailyCarts();
    void addFoodToDailyCartFood(int userId, int foodId, DailyCartFoodCreateDto dto);
    void removeFoodFromDailyCartFood(int userId, int foodId);
    void updateDailyCartFood(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
    DailyFoodsResponseDto getFoodsFromDailyCartFood(int userId);
}

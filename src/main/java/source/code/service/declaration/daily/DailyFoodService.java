package source.code.service.declaration.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.food.DailyCartFoodCreateDto;
import source.code.dto.request.food.DailyCartFoodGetDto;
import source.code.dto.response.daily.DailyFoodsResponseDto;

public interface DailyFoodService {
    void addFoodToDailyCart(int foodId, DailyCartFoodCreateDto dto);

    void removeFoodFromDailyCart(int dailyCartFoodId);

    void updateDailyFoodItem(int dailyCartFoodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    DailyFoodsResponseDto getFoodFromDailyCart(DailyCartFoodGetDto request);
}

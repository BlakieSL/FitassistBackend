package source.code.service.declaration.food;

import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;

import java.util.List;

public interface FoodPopulationService {
    void populate(FoodResponseDto food);

    void populate(List<FoodSummaryDto> foods);
}

package source.code.service.implementation.food;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.food.FoodResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserFoodRepository;
import source.code.service.declaration.food.FoodPopulationService;

@Service
public class FoodPopulationServiceImpl implements FoodPopulationService {
    private final UserFoodRepository userFoodRepository;

    public FoodPopulationServiceImpl(UserFoodRepository userFoodRepository) {
        this.userFoodRepository = userFoodRepository;
    }

    @Override
    public void populate(FoodResponseDto food) {
        int userId = AuthorizationUtil.getUserId();
        SavesProjection savesData = userFoodRepository.findSavesCountAndUserSaved(food.getId(), userId);
        food.setSavesCount(savesData.savesCount());
        food.setSaved(savesData.isSaved());
    }
}

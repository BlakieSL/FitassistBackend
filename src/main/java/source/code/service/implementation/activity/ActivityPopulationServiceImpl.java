package source.code.service.implementation.activity;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserActivityRepository;
import source.code.service.declaration.activity.ActivityPopulationService;

@Service
public class ActivityPopulationServiceImpl implements ActivityPopulationService {
    private final UserActivityRepository userActivityRepository;

    public ActivityPopulationServiceImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public void populate(ActivityResponseDto activity) {
        int userId = AuthorizationUtil.getUserId();

        SavesProjection savesData = userActivityRepository.findSavesCountAndUserSaved(activity.getId(), userId);
        activity.setSavesCount(savesData.savesCount());
        activity.setSaved(savesData.isSaved());
    }
}

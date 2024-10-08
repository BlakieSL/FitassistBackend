package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;

public interface UserPlanService {
    void savePlanToUser(int planId, int userId, short type);
    void deleteSavedPlanFromUser(int planId, int userId, short type);
    LikesAndSavesResponseDto calculatePlanLikesAndSaves(int planId);
}
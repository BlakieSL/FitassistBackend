package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;

public interface UserActivityService {
    void saveActivityToUser(int activityId, int userId, short type);
    void deleteSavedActivityFromUser(int activityId, int userId, short type);
    LikesAndSavesResponseDto calculateActivityLikesAndSaves(int activityId);
}

package source.code.service.declaration.User;

import source.code.dto.response.ActivityResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;

import java.util.List;

public interface UserActivityService {
  void saveActivityToUser(int activityId, int userId, short type);

  void deleteSavedActivityFromUser(int activityId, int userId, short type);
  List<ActivityResponseDto> getActivitiesByUserAndType(int userId, short type);
  LikesAndSavesResponseDto calculateActivityLikesAndSaves(int activityId);
}

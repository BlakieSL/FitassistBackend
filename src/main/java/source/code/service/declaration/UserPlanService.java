package source.code.service.declaration;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.PlanResponseDto;

import java.util.List;

public interface UserPlanService {
  void savePlanToUser(int planId, int userId, short type);

  void deleteSavedPlanFromUser(int planId, int userId, short type);

  List<PlanResponseDto> getPlansByUserAndType(int userId, short type);

  LikesAndSavesResponseDto calculatePlanLikesAndSaves(int planId);
}
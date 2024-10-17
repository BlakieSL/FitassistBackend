package source.code.controller.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.implementation.User.UserPlanServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-plans")
public class UserPlanController {
  private final UserPlanServiceImpl userPlanService;

  public UserPlanController(UserPlanServiceImpl userPlanService) {
    this.userPlanService = userPlanService;
  }

  @GetMapping("/users/{userId}/type/{type}")
  public ResponseEntity<List<PlanResponseDto>> getPlansByUserAndType(@PathVariable int userId,
                                                                     @PathVariable short type) {
    List<PlanResponseDto> plans = userPlanService.getPlansByUserAndType(userId, type);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/plans/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getPlanLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userPlanService.calculatePlanLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/users/{userId}/plans/{planId}/type/{typeId}")
  public ResponseEntity<Void> savePlanToUser(
          @PathVariable int userId, @PathVariable int planId, @PathVariable short typeId) {

    userPlanService.savePlanToUser(userId, planId, typeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/users/{userId}/plans/{planId}/type/{typeId}")
  public ResponseEntity<Void> deleteSavedPlanFromUser(
          @PathVariable int userId, @PathVariable int planId, @PathVariable short typeId) {

    userPlanService.deleteSavedPlanFromUser(planId, userId, typeId);
    return ResponseEntity.ok().build();
  }

}

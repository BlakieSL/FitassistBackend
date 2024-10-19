package source.code.controller.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.ActivityResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.declaration.User.UserActivityService;

import java.util.List;


@RestController
@RequestMapping(path = "/api/user-activities")
public class UserActivityController {
  private final UserActivityService userActivityService;

  public UserActivityController(UserActivityService userActivityService) {
    this.userActivityService = userActivityService;
  }

  @GetMapping("/users/{userId}/type/{type}")
  public ResponseEntity<List<ActivityResponseDto>> getActivitiesByUserAndType(@PathVariable int userId,
                                                                              @PathVariable short type) {
    List<ActivityResponseDto> recipes = userActivityService.getActivitiesByUserAndType(userId, type);
    return ResponseEntity.ok(recipes);
  }

  @GetMapping("/activities/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getActivityLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userActivityService.calculateActivityLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }
  @PostMapping("/users/{userId}/activities/{activityId}/type/{typeId}")
  public ResponseEntity<Void> saveActivityToUser(
          @PathVariable int userId, @PathVariable int activityId, @PathVariable short typeId) {

    userActivityService.saveActivityToUser(userId, activityId, typeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/users/{userId}/activities/{activityId}/type/{typeId}")
  public ResponseEntity<Void> deleteSavedActivityFromUser(
          @PathVariable int userId, @PathVariable int activityId, @PathVariable short typeId) {

    userActivityService.deleteSavedActivityFromUser(activityId, userId, typeId);
    return ResponseEntity.ok().build();
  }
}

package source.code.controller.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.ExerciseResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.service.declaration.UserExerciseService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-exercises")
public class UserExerciseController {
  private final UserExerciseService userExerciseService;

  public UserExerciseController(UserExerciseService userExerciseService) {
    this.userExerciseService = userExerciseService;
  }
  @GetMapping("/users/{userId}/type/{type}")
  public ResponseEntity<List<ExerciseResponseDto>> getExercisesByUserAndType(@PathVariable int userId,
                                                                             @PathVariable short type) {
    List<ExerciseResponseDto> exercises = userExerciseService.getExercisesByUserAndType(userId, type);
    return ResponseEntity.ok(exercises);
  }

  @GetMapping("/exercises/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getExerciseLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userExerciseService.calculateExerciseLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/users/{userId}/exercises/{exerciseId}/type/{typeId}")
  public ResponseEntity<Void> saveExerciseToUser(
          @PathVariable int userId, @PathVariable int exerciseId, @PathVariable short typeId) {

    userExerciseService.saveExerciseToUser(userId, exerciseId, typeId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/users/{userId}/exercises/{exerciseId}/type/{typeId}")
  public ResponseEntity<Void> deleteSavedExerciseFromUser(
          @PathVariable int userId, @PathVariable int exerciseId, @PathVariable short typeId) {

    userExerciseService.deleteSavedExerciseFromUser(exerciseId, userId, typeId);
    return ResponseEntity.ok().build();
  }
}

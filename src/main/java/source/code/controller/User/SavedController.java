package source.code.controller.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.enumerators.EntityType;
import source.code.model.User.BaseUserEntity;
import source.code.service.declaration.Selector.SavedSelectorService;
import source.code.service.declaration.User.SavedService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-saved")
public class SavedController {
  private final SavedSelectorService savedSelectorService;

  public SavedController(SavedSelectorService savedSelectorService) {
    this.savedSelectorService = savedSelectorService;
  }

  @GetMapping("/item-type/{itemType}/users/{userId}/type/{type}")
  public ResponseEntity<List<BaseUserEntity>> getAllFromUser(@PathVariable EntityType itemType,
                                                             @PathVariable int userId,
                                                             @PathVariable short type) {
    SavedService savedService = savedSelectorService.getService(itemType);
    List<BaseUserEntity> dto =  savedService.getAllFromUser(userId, type);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/item-type/{itemType}/{itemId}/likes-ans-saves")
  public ResponseEntity<LikesAndSavesResponseDto> calculateLikesAndSaves(@PathVariable EntityType itemType,
                                                                         @PathVariable int itemId) {
    SavedService savedService = savedSelectorService.getService(itemType);
    LikesAndSavesResponseDto dto = savedService.calculateLikesAndSaves(itemId);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/item-type/{itemType}/{itemId}/users/{userId}/type/{type}")
  public ResponseEntity<Void> saveToUser(@PathVariable EntityType itemType,
                                         @PathVariable int itemId,
                                         @PathVariable int userId,
                                         @PathVariable short type) {
    SavedService savedService = savedSelectorService.getService(itemType);
    savedService.saveToUser(userId, itemId, type);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/item-type/{itemType}/{itemId}/users/{userId}/type/{type}")
  public ResponseEntity<Void> deleteFromUser(@PathVariable EntityType itemType,
                                             @PathVariable int itemId,
                                             @PathVariable int userId,
                                             @PathVariable short type) {
    SavedService savedService = savedSelectorService.getService(itemType);
    savedService.deleteFromUser(userId, itemId, type);
    return ResponseEntity.noContent().build();
  }
}

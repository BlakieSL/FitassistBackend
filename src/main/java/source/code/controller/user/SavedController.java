package source.code.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.Enum.SavedEntityType;
import source.code.model.user.BaseUserEntity;
import source.code.service.declaration.selector.SavedSelectorService;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-saved")
public class SavedController {
    private final SavedSelectorService savedSelectorService;

    public SavedController(SavedSelectorService savedSelectorService) {
        this.savedSelectorService = savedSelectorService;
    }

    @GetMapping("/item-type/{itemType}/users/{userId}/type/{type}")
    public ResponseEntity<List<BaseUserEntity>> getAllFromUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable int userId,
            @PathVariable short type
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        List<BaseUserEntity> dto = savedService.getAllFromUser(userId, type);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/item-type/{itemType}/{itemId}/likes-ans-saves")
    public ResponseEntity<LikesAndSavesResponseDto> calculateLikesAndSaves(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        LikesAndSavesResponseDto dto = savedService.calculateLikesAndSaves(itemId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/item-type/{itemType}/{itemId}/users/{userId}/type/{type}")
    public ResponseEntity<Void> saveToUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable int userId,
            @PathVariable short type
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        savedService.saveToUser(userId, itemId, type);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/item-type/{itemType}/{itemId}/users/{userId}/type/{type}")
    public ResponseEntity<Void> deleteFromUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable int userId,
            @PathVariable short type
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        savedService.deleteFromUser(userId, itemId, type);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/item-type/{itemType}/users/{userId}")
    public ResponseEntity<List<BaseUserEntity>> getAllFromUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int userId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        List<BaseUserEntity> dto = savedServiceWithoutType.getAllFromUser(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/item-type/{itemType}/{itemId}/users/{userId}")
    public ResponseEntity<Void> saveToUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable int userId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        savedServiceWithoutType.saveToUser(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/item-type/{itemType}/{itemId}/users/{userId}")
    public ResponseEntity<Void> deleteFromUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable int userId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        savedServiceWithoutType.deleteFromUser(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/item-type/{itemType}/{itemId}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> calculateLikesAndSavesWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        LikesAndSavesResponseDto dto = savedServiceWithoutType.calculateLikesAndSaves(itemId);
        return ResponseEntity.ok(dto);
    }
}

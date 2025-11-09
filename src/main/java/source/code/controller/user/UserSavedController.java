package source.code.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.model.SavedEntityType;
import source.code.model.user.TypeOfInteraction;
import source.code.service.declaration.selector.SavedSelectorService;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user-saved")
public class UserSavedController {
    private final SavedSelectorService savedSelectorService;

    public UserSavedController(SavedSelectorService savedSelectorService) {
        this.savedSelectorService = savedSelectorService;
    }

    @GetMapping("/item-type/{itemType}/type/{type}/user/{userId}")
    public ResponseEntity<List<BaseUserEntity>> getAllFromUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable TypeOfInteraction type,
            @PathVariable int userId
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

    @PostMapping("/item-type/{itemType}/{itemId}/type/{type}")
    public ResponseEntity<Void> saveToUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable TypeOfInteraction type
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        savedService.saveToUser(itemId, type);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/item-type/{itemType}/{itemId}/type/{type}")
    public ResponseEntity<Void> deleteFromUser(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId,
            @PathVariable TypeOfInteraction type
    ) {
        SavedService savedService = savedSelectorService.getService(itemType);
        savedService.deleteFromUser(itemId, type);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/item-type/{itemType}/user/{userId}")
    public ResponseEntity<List<BaseUserEntity>> getAllFromUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable("userId") int userId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        List<BaseUserEntity> dto = savedServiceWithoutType.getAllFromUser(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/item-type/{itemType}/{itemId}")
    public ResponseEntity<Void> saveToUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        savedServiceWithoutType.saveToUser(itemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/item-type/{itemType}/{itemId}")
    public ResponseEntity<Void> deleteFromUserWithoutType(
            @PathVariable SavedEntityType itemType,
            @PathVariable int itemId
    ) {
        SavedServiceWithoutType savedServiceWithoutType = savedSelectorService
                .getServiceWithoutType(itemType);
        savedServiceWithoutType.deleteFromUser(itemId);
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

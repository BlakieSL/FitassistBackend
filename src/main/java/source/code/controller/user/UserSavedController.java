package source.code.controller.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.model.SavedEntityType;
import source.code.model.user.TypeOfInteraction;
import source.code.service.declaration.selector.SavedSelectorService;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

@RestController
@RequestMapping(path = "/api/user-saved")
public class UserSavedController {

	private final SavedSelectorService savedSelectorService;

	public UserSavedController(SavedSelectorService savedSelectorService) {
		this.savedSelectorService = savedSelectorService;
	}

	@GetMapping("/item-type/{itemType}/type/{type}/user/{userId}")
	public ResponseEntity<Page<BaseUserEntity>> getAllFromUser(@PathVariable SavedEntityType itemType,
			@PathVariable TypeOfInteraction type, @PathVariable int userId,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		SavedService savedService = savedSelectorService.getService(itemType);
		Page<BaseUserEntity> dto = savedService.getAllFromUser(userId, type, pageable);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/item-type/{itemType}/{itemId}/type/{type}")
	public ResponseEntity<Void> saveToUser(@PathVariable SavedEntityType itemType, @PathVariable int itemId,
			@PathVariable TypeOfInteraction type) {
		SavedService savedService = savedSelectorService.getService(itemType);
		savedService.saveToUser(itemId, type);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/item-type/{itemType}/{itemId}/type/{type}")
	public ResponseEntity<Void> deleteFromUser(@PathVariable SavedEntityType itemType, @PathVariable int itemId,
			@PathVariable TypeOfInteraction type) {
		SavedService savedService = savedSelectorService.getService(itemType);
		savedService.deleteFromUser(itemId, type);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/item-type/{itemType}/user/{userId}")
	public ResponseEntity<Page<BaseUserEntity>> getAllFromUserWithoutType(@PathVariable SavedEntityType itemType,
			@PathVariable("userId") int userId,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		Page<BaseUserEntity> dto = savedServiceWithoutType.getAllFromUser(userId, pageable);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/item-type/{itemType}/{itemId}")
	public ResponseEntity<Void> saveToUserWithoutType(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		savedServiceWithoutType.saveToUser(itemId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/item-type/{itemType}/{itemId}")
	public ResponseEntity<Void> deleteFromUserWithoutType(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		savedServiceWithoutType.deleteFromUser(itemId);
		return ResponseEntity.noContent().build();
	}

}

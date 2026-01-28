package com.fitassist.backend.controller.user;

import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.model.user.TypeOfInteraction;
import com.fitassist.backend.service.declaration.selector.SavedSelectorService;
import com.fitassist.backend.service.declaration.user.SavedService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import com.fitassist.backend.service.implementation.selector.SavedEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user-saved")
public class UserSavedController {

	private final SavedSelectorService savedSelectorService;

	public UserSavedController(SavedSelectorService savedSelectorService) {
		this.savedSelectorService = savedSelectorService;
	}

	@GetMapping("/item-type/{itemType}/type/{type}/user/{userId}")
	public ResponseEntity<Page<UserEntitySummaryResponseDto>> getAllFromUser(@PathVariable SavedEntityType itemType,
			@PathVariable TypeOfInteraction type, @PathVariable int userId,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		SavedService savedService = savedSelectorService.getService(itemType);
		Page<UserEntitySummaryResponseDto> dto = savedService.getAllFromUser(userId, type, pageable);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/item-type/{itemType}/{itemId}/type/{type}")
	public ResponseEntity<InteractionResponseDto> saveToUser(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId, @PathVariable TypeOfInteraction type) {
		SavedService savedService = savedSelectorService.getService(itemType);
		InteractionResponseDto response = savedService.saveToUser(itemId, type);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/item-type/{itemType}/{itemId}/type/{type}")
	public ResponseEntity<InteractionResponseDto> deleteFromUser(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId, @PathVariable TypeOfInteraction type) {
		SavedService savedService = savedSelectorService.getService(itemType);
		InteractionResponseDto response = savedService.deleteFromUser(itemId, type);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/item-type/{itemType}/user/{userId}")
	public ResponseEntity<Page<UserEntitySummaryResponseDto>> getAllFromUserWithoutType(
			@PathVariable SavedEntityType itemType, @PathVariable("userId") int userId,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		Page<UserEntitySummaryResponseDto> dto = savedServiceWithoutType.getAllFromUser(userId, pageable);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/item-type/{itemType}/{itemId}")
	public ResponseEntity<InteractionResponseDto> saveToUserWithoutType(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		InteractionResponseDto response = savedServiceWithoutType.saveToUser(itemId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/item-type/{itemType}/{itemId}")
	public ResponseEntity<InteractionResponseDto> deleteFromUserWithoutType(@PathVariable SavedEntityType itemType,
			@PathVariable int itemId) {
		SavedServiceWithoutType savedServiceWithoutType = savedSelectorService.getServiceWithoutType(itemType);
		InteractionResponseDto response = savedServiceWithoutType.deleteFromUser(itemId);
		return ResponseEntity.ok(response);
	}

}

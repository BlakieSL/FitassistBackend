package com.fitassist.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.annotation.AdminOnly;
import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.CalculateActivityCaloriesRequestDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.service.declaration.activity.ActivityService;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/activities")
public class ActivityController {

	private final ActivityService activityService;

	public ActivityController(ActivityService activityService) {
		this.activityService = activityService;
	}

	@AdminOnly
	@PostMapping
	public ResponseEntity<ActivityResponseDto> createActivity(@Valid @RequestBody ActivityCreateDto dto) {
		ActivityResponseDto response = activityService.createActivity(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@AdminOnly
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateActivity(@PathVariable int id, @RequestBody JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		activityService.updateActivity(id, patch);
		return ResponseEntity.noContent().build();
	}

	@AdminOnly
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteActivity(@PathVariable int id) {
		activityService.deleteActivity(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ActivityResponseDto> getActivity(@PathVariable int id) {
		ActivityResponseDto activity = activityService.getActivity(id);
		return ResponseEntity.ok(activity);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<ActivitySummaryDto>> getFilteredActivities(@Valid @RequestBody FilterDto filterDto,
			@PageableDefault(size = 100, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<ActivitySummaryDto> filteredActivities = activityService.getFilteredActivities(filterDto, pageable);
		return ResponseEntity.ok(filteredActivities);
	}

	@PostMapping("/{id}/calculate-calories")
	public ResponseEntity<ActivityCalculatedResponseDto> calculateActivityCaloriesBurned(@PathVariable int id,
			@Valid @RequestBody CalculateActivityCaloriesRequestDto request) {
		ActivityCalculatedResponseDto response = activityService.calculateCaloriesBurned(id, request);
		return ResponseEntity.ok(response);
	}

}

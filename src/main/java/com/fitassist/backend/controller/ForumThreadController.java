package com.fitassist.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitassist.backend.annotation.ThreadOwnerOrAdminOrModerator;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.service.declaration.forumthread.ForumThreadService;

@RestController
@RequestMapping("/api/threads")
public class ForumThreadController {

	private final ForumThreadService forumThreadService;

	public ForumThreadController(ForumThreadService forumThreadService) {
		this.forumThreadService = forumThreadService;
	}

	@PostMapping
	public ResponseEntity<ForumThreadResponseDto> createForumThread(
			@Valid @RequestBody ForumThreadCreateDto createDto) {
		ForumThreadResponseDto responseDto = forumThreadService.createForumThread(createDto);
		return ResponseEntity.ok(responseDto);
	}

	@ThreadOwnerOrAdminOrModerator
	@PatchMapping("/{forumThreadId}")
	public ResponseEntity<Void> updateForumThread(@PathVariable int forumThreadId, @RequestBody JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		forumThreadService.updateForumThread(forumThreadId, patch);
		return ResponseEntity.ok().build();
	}

	@ThreadOwnerOrAdminOrModerator
	@DeleteMapping("/{forumThreadId}")
	public ResponseEntity<Void> deleteForumThread(@PathVariable int forumThreadId) {
		forumThreadService.deleteForumThread(forumThreadId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{forumThreadId}")
	public ResponseEntity<ForumThreadResponseDto> getForumThread(@PathVariable int forumThreadId) {
		ForumThreadResponseDto responseDto = forumThreadService.getForumThread(forumThreadId);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<ForumThreadSummaryDto>> getFilteredForumThreads(@Valid @RequestBody FilterDto filterDto,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ForumThreadSummaryDto> threads = forumThreadService.getFilteredForumThreads(filterDto, pageable);
		return ResponseEntity.ok(threads);
	}

}

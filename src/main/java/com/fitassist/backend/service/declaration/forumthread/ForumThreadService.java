package com.fitassist.backend.service.declaration.forumthread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ForumThreadService {

	ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto);

	void updateForumThread(int forumThreadId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteForumThread(int forumThreadId);

	ForumThreadResponseDto getForumThread(int forumThreadId);

	Page<ForumThreadSummaryDto> getFilteredForumThreads(FilterDto filter, Pageable pageable);

}

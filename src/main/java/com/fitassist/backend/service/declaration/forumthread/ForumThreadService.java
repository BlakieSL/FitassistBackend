package com.fitassist.backend.service.declaration.forumthread;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.forumThread.ForumThreadCreateDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ForumThreadService {

	ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto);

	void updateForumThread(int forumThreadId, JsonMergePatch patch) throws JacksonException;

	void deleteForumThread(int forumThreadId);

	ForumThreadResponseDto getForumThread(int forumThreadId);

	Page<ForumThreadSummaryDto> getFilteredForumThreads(FilterDto filter, Pageable pageable);

}

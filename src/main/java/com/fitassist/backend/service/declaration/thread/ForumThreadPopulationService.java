package com.fitassist.backend.service.declaration.thread;

import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;

import java.util.List;

public interface ForumThreadPopulationService {

	void populate(List<ForumThreadSummaryDto> threads);

	void populate(ForumThreadResponseDto thread);

}

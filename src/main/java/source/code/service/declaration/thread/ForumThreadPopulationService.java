package source.code.service.declaration.thread;

import java.util.List;

import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;

public interface ForumThreadPopulationService {

	void populate(List<ForumThreadSummaryDto> threads);

	void populate(ForumThreadResponseDto thread);

}

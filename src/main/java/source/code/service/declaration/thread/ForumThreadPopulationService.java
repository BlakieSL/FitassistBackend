package source.code.service.declaration.thread;

import source.code.dto.response.forumThread.ForumThreadSummaryDto;

import java.util.List;

public interface ForumThreadPopulationService {
    void populate(List<ForumThreadSummaryDto> threads);
}

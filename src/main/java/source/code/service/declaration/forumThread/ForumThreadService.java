package source.code.service.declaration.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;

public interface ForumThreadService {
    ForumThreadResponseDto createForumThread(ForumThreadCreateDto createDto);

    void updateForumThread(int forumThreadId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException;

    void deleteForumThread(int forumThreadId);

    ForumThreadResponseDto getForumThread(int forumThreadId);

    Page<ForumThreadSummaryDto> getFilteredForumThreads(FilterDto filter, Pageable pageable);
}

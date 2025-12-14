package source.code.service.declaration.comment;

import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;

import java.util.List;

public interface CommentPopulationService {
    void populate(List<CommentSummaryDto> summaries);

    void populate(CommentResponseDto comment);

    void populateList(List<CommentResponseDto> comments);
}

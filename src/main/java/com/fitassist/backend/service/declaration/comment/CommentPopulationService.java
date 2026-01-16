package com.fitassist.backend.service.declaration.comment;

import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;

import java.util.List;

public interface CommentPopulationService {

	void populate(List<CommentSummaryDto> summaries);

	void populate(CommentResponseDto comment);

	void populateList(List<CommentResponseDto> comments);

}

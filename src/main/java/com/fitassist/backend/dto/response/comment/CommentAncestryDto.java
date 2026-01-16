package com.fitassist.backend.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAncestryDto {

	private Integer threadId;

	private List<Integer> ancestorCommentIds;

}

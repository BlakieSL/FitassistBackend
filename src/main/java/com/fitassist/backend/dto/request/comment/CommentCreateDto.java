package com.fitassist.backend.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentCreateDto {

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	private String text;

	@NotNull
	private Integer threadId;

	private Integer parentCommentId;

}

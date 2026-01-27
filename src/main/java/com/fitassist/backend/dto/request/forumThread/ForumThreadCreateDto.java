package com.fitassist.backend.dto.request.forumThread;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String title;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	private String text;

	@NotNull
	private int threadCategoryId;

}

package com.fitassist.backend.dto.request.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadUpdateDto {

	private String title;

	private String text;

	private Integer threadCategoryId;

}

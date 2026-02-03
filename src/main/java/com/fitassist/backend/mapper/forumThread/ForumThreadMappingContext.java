package com.fitassist.backend.mapper.forumThread;

import com.fitassist.backend.model.thread.ThreadCategory;
import com.fitassist.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForumThreadMappingContext {

	private final User user;

	private final ThreadCategory category;

}

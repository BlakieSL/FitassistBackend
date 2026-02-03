package com.fitassist.backend.mapper.comment;

import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentMappingContext {

	private final User user;

	private final ForumThread thread;

	private final Comment parentComment;

}

package com.fitassist.backend.mapper.complaint;

import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComplaintMappingContext {

	private final User user;

	private final Comment comment;

	private final ForumThread thread;

	public static ComplaintMappingContext forCommentComplaint(User user, Comment comment) {
		return new ComplaintMappingContext(user, comment, null);
	}

	public static ComplaintMappingContext forThreadComplaint(User user, ForumThread thread) {
		return new ComplaintMappingContext(user, null, thread);
	}

}

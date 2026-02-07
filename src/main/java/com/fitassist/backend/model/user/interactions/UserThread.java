package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_thread")
@Getter
@Setter
public class UserThread extends UserInteractionBase {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "thread_id", nullable = false)
	private ForumThread forumThread;

	public static UserThread of(User user, ForumThread forumThread) {
		UserThread userThread = new UserThread();
		userThread.setUser(user);
		userThread.setForumThread(forumThread);
		return userThread;
	}

}

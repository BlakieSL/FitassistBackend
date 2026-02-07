package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_comment")
@Getter
@Setter
public class UserComment extends UserInteractionWithType {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;

	public static UserComment of(User user, Comment comment, TypeOfInteraction typeOfInteraction) {
		UserComment userComment = new UserComment();
		userComment.setUser(user);
		userComment.setComment(comment);
		userComment.setType(typeOfInteraction);
		return userComment;
	}

}

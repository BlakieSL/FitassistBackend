package com.fitassist.backend.model.thread;

import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserComment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comment")
@NamedEntityGraph(name = "Comment.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Comment.withAssociations", includeAllAttributes = true)
@NamedEntityGraph(name = "Comment.summary",
		attributeNodes = { @NamedAttributeNode("user"), @NamedAttributeNode("thread") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(nullable = false)
	private String text;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "thread_id", nullable = false)
	private ForumThread thread;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private Comment parentComment;

	@OneToMany(mappedBy = "parentComment")
	private final Set<Comment> replies = new HashSet<>();

	@OneToMany(mappedBy = "comment")
	private final Set<UserComment> userCommentLikes = new HashSet<>();

	@OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
	private final Set<CommentComplaint> complaints = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'COMMENT'")
	private List<Media> mediaList = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public static Comment of(Integer id, User user) {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setUser(user);
		return comment;
	}

}

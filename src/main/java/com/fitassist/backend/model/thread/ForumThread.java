package com.fitassist.backend.model.thread;

import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserThread;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;

@Entity
@Table(name = "thread")
@NamedEntityGraph(name = "Thread.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "ForumThread.summary",
		attributeNodes = { @NamedAttributeNode("user"), @NamedAttributeNode("threadCategory") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThread {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String title;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	@Column(nullable = false, length = TEXT_MAX_LENGTH)
	private String text;

	@Column(nullable = false)
	private long views = 0L;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "thread_category_id", nullable = false)
	private ThreadCategory threadCategory;

	@OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE)
	private final Set<Comment> comments = new HashSet<>();

	@OneToMany(mappedBy = "forumThread", cascade = CascadeType.REMOVE)
	private final Set<UserThread> userThreads = new HashSet<>();

	@OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE)
	private final Set<ThreadComplaint> complaints = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'FORUM_THREAD'")
	private List<Media> mediaList = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static ForumThread of(Integer id, User user) {
		ForumThread forumThread = new ForumThread();
		forumThread.setId(id);
		forumThread.setUser(user);
		return forumThread;
	}

}

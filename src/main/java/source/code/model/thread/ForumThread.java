package source.code.model.thread;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserThread;

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

	private static final int TITLE_MAX_LENGTH = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = TITLE_MAX_LENGTH)
	@Column(nullable = false)
	private String title;

	@NotNull
	@PastOrPresent
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotBlank
	@Column(nullable = false, columnDefinition = "TEXT")
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

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'FORUM_THREAD'")
	private List<Media> mediaList = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public static ForumThread of(Integer id, User user) {
		ForumThread forumThread = new ForumThread();
		forumThread.setId(id);
		forumThread.setUser(user);
		return forumThread;
	}

}

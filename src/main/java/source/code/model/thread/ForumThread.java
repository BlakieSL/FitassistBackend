package source.code.model.thread;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;
import source.code.model.user.UserThread;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thread")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThread {
    private static final int TITLE_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = TITLE_MAX_LENGTH)
    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @NotBlank
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int views = 0;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "thread_category_id", nullable = false)
    private ThreadCategory threadCategory;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE)
    private final Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "forumThread", cascade = CascadeType.REMOVE)
    private final Set<UserThread> userThreads = new HashSet<>();

    //image

    public void incrementViews() {
        views++;
    }

    @PrePersist
    public void prePersist() {
        this.dateCreated = LocalDateTime.now();
    }

    public static ForumThread of(Integer id, User user) {
        ForumThread forumThread = new ForumThread();
        forumThread.setId(id);
        forumThread.setUser(user);
        return forumThread;
    }
}

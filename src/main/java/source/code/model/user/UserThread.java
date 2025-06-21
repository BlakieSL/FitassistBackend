package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.thread.ForumThread;

@Entity
@Table(name = "user_thread")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UserThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

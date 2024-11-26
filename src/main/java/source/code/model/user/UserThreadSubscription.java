package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.forum.ForumThread;
import source.code.model.user.profile.User;

@Entity
@Table(name = "user_thread")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UserThreadSubscription {
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

    public static UserThreadSubscription of(User user, ForumThread forumThread) {
        UserThreadSubscription userThreadSubscription = new UserThreadSubscription();
        userThreadSubscription.setUser(user);
        userThreadSubscription.setForumThread(forumThread);
        return userThreadSubscription;
    }
}

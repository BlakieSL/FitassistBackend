package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.forum.ForumThread;

@Entity
@Table(name = "user_thread")
@Getter
@Setter
@AllArgsConstructor
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
}

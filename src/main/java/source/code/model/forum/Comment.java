package source.code.model.forum;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;
import source.code.model.user.UserCommentLikes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment")
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
    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

   @ManyToOne
   @JoinColumn(name = "parent_comment_id")
   private Comment parentComment;

   @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
   private final Set<Comment> replies = new HashSet<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private final Set<UserCommentLikes> userCommentLikes = new HashSet<>();

    public static Comment of(Integer id, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        return comment;
    }
}

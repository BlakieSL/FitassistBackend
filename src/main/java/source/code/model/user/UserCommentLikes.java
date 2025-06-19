package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.forum.Comment;

@Entity
@Table(name = "user_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserCommentLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public static UserCommentLikes of(User user, Comment comment) {
        UserCommentLikes userCommentLikes = new UserCommentLikes();
        userCommentLikes.setUser(user);
        userCommentLikes.setComment(comment);
        return userCommentLikes;
    }
}

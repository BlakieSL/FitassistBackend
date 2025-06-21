package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.thread.Comment;

@Entity
@Table(name = "user_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserComment {
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOfInteraction type;

    public static UserComment of(User user, Comment comment, TypeOfInteraction typeOfInteraction) {
        UserComment userComment = new UserComment();
        userComment.setUser(user);
        userComment.setComment(comment);
        userComment.setType(typeOfInteraction);
        return userComment;
    }
}

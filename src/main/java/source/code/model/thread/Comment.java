package source.code.model.thread;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserComment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comment")
@NamedEntityGraph(name = "Comment.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Comment.withAssociations", includeAllAttributes = true)
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
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime dateCreated;

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

   @OneToMany(mappedBy = "parentComment")
   private final Set<Comment> replies = new HashSet<>();

    @OneToMany(mappedBy = "comment")
    private final Set<UserComment> userCommentLikes = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "parent_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("parentType = 'COMMENT'")
    private List<Media> mediaList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dateCreated = LocalDateTime.now();
    }

    public static Comment of(Integer id, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        return comment;
    }
}

package source.code.model.forum;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;

@Entity
@DiscriminatorValue("COMMENT_COMPLAINT")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentComplaint extends ComplaintBase {
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public static CommentComplaint of(Integer id, User user) {
        CommentComplaint complaint = new CommentComplaint();
        complaint.setId(id);
        complaint.setUser(user);
        return complaint;
    }
}

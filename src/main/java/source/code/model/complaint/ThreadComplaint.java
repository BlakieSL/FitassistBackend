package source.code.model.complaint;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.model.media.Media;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("THREAD_COMPLAINT")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ThreadComplaint extends ComplaintBase {
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private ForumThread thread;

    @OneToMany
    @JoinColumn(name = "parent_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("parentType = 'THREAD_COMPLAINT'")
    private List<Media> mediaList = new ArrayList<>();

    public static ThreadComplaint of(Integer id, User user) {
        ThreadComplaint complaint = new ThreadComplaint();
        complaint.setId(id);
        complaint.setUser(user);
        return complaint;
    }
}

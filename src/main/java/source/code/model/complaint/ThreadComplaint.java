package source.code.model.complaint;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.thread.ForumThread;
import source.code.model.user.User;

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

    public static ThreadComplaint of(Integer id, User user) {
        ThreadComplaint complaint = new ThreadComplaint();
        complaint.setId(id);
        complaint.setUser(user);
        return complaint;
    }
}

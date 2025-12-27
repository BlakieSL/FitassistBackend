package source.code.model.complaint;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.model.media.Media;
import source.code.model.thread.Comment;
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

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'COMMENT_COMPLAINT'")
	private List<Media> mediaList = new ArrayList<>();

	public static CommentComplaint of(Integer id, User user) {
		CommentComplaint complaint = new CommentComplaint();
		complaint.setId(id);
		complaint.setUser(user);
		return complaint;
	}

}

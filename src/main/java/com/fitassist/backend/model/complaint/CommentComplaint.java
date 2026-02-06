package com.fitassist.backend.model.complaint;

import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("COMMENT_COMPLAINT")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentComplaint extends ComplaintBase {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'COMMENT_COMPLAINT'")
	private List<Media> mediaList = new ArrayList<>();

	@Override
	public String getDiscriminatorValue() {
		return "COMMENT_COMPLAINT";
	}

	@Override
	public Integer getAssociatedId() {
		return comment.getId();
	}

	public static CommentComplaint of(Integer id, User user) {
		CommentComplaint complaint = new CommentComplaint();
		complaint.setId(id);
		complaint.setUser(user);
		return complaint;
	}

}

package com.fitassist.backend.model.complaint;

import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.thread.ForumThread;
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
@DiscriminatorValue("THREAD_COMPLAINT")
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ThreadComplaint extends ComplaintBase {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "thread_id")
	private ForumThread thread;

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'THREAD_COMPLAINT'")
	private List<Media> mediaList = new ArrayList<>();

	@Override
	public String getDiscriminatorValue() {
		return "THREAD_COMPLAINT";
	}

	@Override
	public Integer getAssociatedId() {
		return thread.getId();
	}

	public static ThreadComplaint of(Integer id, User user) {
		ThreadComplaint complaint = new ThreadComplaint();
		complaint.setId(id);
		complaint.setUser(user);
		return complaint;
	}

}

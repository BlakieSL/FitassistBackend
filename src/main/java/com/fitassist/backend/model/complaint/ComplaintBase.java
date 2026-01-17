package com.fitassist.backend.model.complaint;

import com.fitassist.backend.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "complaint")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@NamedEntityGraph(name = "ComplaintBase.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ComplaintBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ComplaintReason reason;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ComplaintStatus status = ComplaintStatus.PENDING;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

}

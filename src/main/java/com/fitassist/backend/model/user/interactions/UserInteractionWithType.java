package com.fitassist.backend.model.user.interactions;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class UserInteractionWithType extends UserInteractionBase {

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TypeOfInteraction type;

}

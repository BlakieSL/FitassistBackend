package com.fitassist.backend.dto.request.complaint;

import com.fitassist.backend.model.complaint.ComplaintReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ComplaintCreateDto {

	@NotNull
	private ComplaintReason reason;

	@NotNull
	private Integer parentId;

	@NotNull
	private ComplaintSubClass subClass;

}

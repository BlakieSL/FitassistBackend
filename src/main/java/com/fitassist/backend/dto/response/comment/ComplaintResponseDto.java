package com.fitassist.backend.dto.response.comment;

import com.fitassist.backend.model.complaint.ComplaintReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintResponseDto implements Serializable {

	private Integer id;

	private ComplaintReason reason;

	private String status;

	private Integer userId;

	private String discriminatorValue;

	private Integer associatedId;

}

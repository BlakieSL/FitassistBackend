package com.fitassist.backend.dto.response.other;

import com.fitassist.backend.model.media.MediaConnectedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDto implements Serializable {

	private Integer id;

	private String imageUrl;

	private MediaConnectedEntity parentType;

	private Integer parentId;

}

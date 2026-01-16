package com.fitassist.backend.dto.response.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class LikesAndSavesResponseDto implements Serializable {

	private long likes;

	private long saves;

}

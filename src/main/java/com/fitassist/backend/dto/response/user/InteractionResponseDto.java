package com.fitassist.backend.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InteractionResponseDto {

	private Boolean liked;

	private Long likesCount;

	private Boolean disliked;

	private Long dislikesCount;

	private Boolean saved;

	private Long savesCount;

}
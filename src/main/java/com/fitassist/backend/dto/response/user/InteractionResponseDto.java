package com.fitassist.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractionResponseDto {

	private boolean interacted;

	private long count;

}
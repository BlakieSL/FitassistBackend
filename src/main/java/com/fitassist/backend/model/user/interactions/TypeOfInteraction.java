package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.dto.response.user.InteractionResponseDto;

public enum TypeOfInteraction {

	LIKE, DISLIKE, SAVE;

	public TypeOfInteraction getOpposite() {
		return switch (this) {
			case LIKE -> DISLIKE;
			case DISLIKE -> LIKE;
			case SAVE -> null;
		};
	}

	public void mapInteraction(InteractionResponseDto dto, boolean interacted, long count) {
		switch (this) {
			case LIKE -> {
				dto.setLiked(interacted);
				dto.setLikesCount(count);
			}
			case DISLIKE -> {
				dto.setDisliked(interacted);
				dto.setDislikesCount(count);
			}
			case SAVE -> {
				dto.setSaved(interacted);
				dto.setSavesCount(count);
			}
		}
	}

}

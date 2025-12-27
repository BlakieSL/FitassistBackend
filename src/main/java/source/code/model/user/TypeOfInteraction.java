package source.code.model.user;

public enum TypeOfInteraction {

	LIKE, DISLIKE, SAVE;

	public TypeOfInteraction getOpposite() {
		return switch (this) {
			case LIKE -> DISLIKE;
			case DISLIKE -> LIKE;
			case SAVE -> null;
		};
	}

}

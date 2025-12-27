package source.code.dto.pojo.projection;

public interface SavesProjection {

	Integer getEntityId();

	Long getSavesCount();

	Long getUserSaved();

	default boolean isSaved() {
		return getUserSaved() != null && getUserSaved() == 1;
	}

	default long savesCount() {
		return getSavesCount() != null ? getSavesCount() : 0L;
	}

}

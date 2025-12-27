package source.code.exception;

import static org.hibernate.internal.util.collections.ArrayHelper.toStringArray;

public class NotUniqueRecordException extends LocalizedException {

	public <T> NotUniqueRecordException(Class<T> entityClass, Object... identifiers) {
		super("NotUniqueRecordException.message", null, entityClass.getSimpleName(),
				String.join(", ", toStringArray(identifiers)));
	}

}

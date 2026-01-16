package com.fitassist.backend.exception;

import static org.hibernate.internal.util.collections.ArrayHelper.toStringArray;

public class RecordNotFoundException extends LocalizedException {

	public <T> RecordNotFoundException(Class<T> entityClass, Object... identifiers) {
		super("RecordNotFoundException.message", null, entityClass.getSimpleName(),
				String.join(", ", toStringArray(identifiers)));
	}

	public static <T> RecordNotFoundException of(Class<T> entityClass, Object... identifiers) {
		return new RecordNotFoundException(entityClass, identifiers);
	}

}

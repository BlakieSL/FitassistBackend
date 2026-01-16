package com.fitassist.backend.service.declaration.helpers;

public interface ValidationService {

	<T> void validate(T dto, Class<?>... groups);

}

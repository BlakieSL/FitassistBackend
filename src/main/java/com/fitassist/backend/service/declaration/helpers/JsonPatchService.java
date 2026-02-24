package com.fitassist.backend.service.declaration.helpers;

import tools.jackson.core.JacksonException;
import jakarta.json.JsonMergePatch;

public interface JsonPatchService {

	<T> T applyPatch(JsonMergePatch patch, Object targetBean, Class<T> beanClass) throws JacksonException;

	<T> T createFromPatch(JsonMergePatch patch, Class<T> beanClass) throws JacksonException;

}

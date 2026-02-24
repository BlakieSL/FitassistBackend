package com.fitassist.backend.service.implementation.helpers;

import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.StringReader;

@Service
public class JsonPatchServiceImpl implements JsonPatchService {

	private final ObjectMapper objectMapper;

	public JsonPatchServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public <T> T applyPatch(JsonMergePatch patch, Object targetBean, Class<T> beanClass) throws JacksonException {
		String targetJson = objectMapper.writeValueAsString(targetBean);
		JsonValue targetValue = readJsonValue(targetJson);
		JsonValue patchedValue = patch.apply(targetValue);
		return objectMapper.readValue(patchedValue.toString(), beanClass);
	}

	@Override
	public <T> T createFromPatch(JsonMergePatch patch, Class<T> beanClass) throws JacksonException {
		JsonValue patchedValue = patch.apply(JsonValue.EMPTY_JSON_OBJECT);
		return objectMapper.readValue(patchedValue.toString(), beanClass);
	}

	private JsonValue readJsonValue(String json) {
		try (JsonReader reader = Json.createReader(new StringReader(json))) {
			return reader.readValue();
		}
	}

}

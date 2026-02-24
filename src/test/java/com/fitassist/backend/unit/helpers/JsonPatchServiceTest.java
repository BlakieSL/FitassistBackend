package com.fitassist.backend.unit.helpers;

import com.fitassist.backend.service.implementation.helpers.JsonPatchServiceImpl;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsonp.JSONPModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JsonPatchServiceTest {

	private JsonPatchServiceImpl jsonPatchService;

	@BeforeEach
	void setUp() {
		jsonPatchService = new JsonPatchServiceImpl(JsonMapper.builder().addModule(new JSONPModule()).build());
	}

	@Test
	void applyPatch_shouldApplyPatchAndReturnPatchedBean() throws JacksonException {
		JsonObject patchJson = Json.createObjectBuilder().add("name", "Updated").build();
		JsonMergePatch patch = Json.createMergePatch(patchJson);

		TestBean target = new TestBean("Original");
		TestBean result = jsonPatchService.applyPatch(patch, target, TestBean.class);

		assertEquals("Updated", result.name());
	}

	@Test
	void createFromPatch_shouldCreateBeanFromPatch() throws JacksonException {
		JsonObject patchJson = Json.createObjectBuilder().add("name", "Created").build();
		JsonMergePatch patch = Json.createMergePatch(patchJson);

		TestBean result = jsonPatchService.createFromPatch(patch, TestBean.class);

		assertEquals("Created", result.name());
	}

	@Test
	void applyPatch_shouldThrowJacksonException_whenDeserializationFails() {
		JsonObject patchJson = Json.createObjectBuilder().add("name", "Updated").build();
		JsonMergePatch patch = Json.createMergePatch(patchJson);

		assertThrows(JacksonException.class,
				() -> jsonPatchService.applyPatch(patch, new TestBean("Original"), Undeserializable.class));
	}

	private record TestBean(String name) {
	}

	private static final class Undeserializable {

		private Undeserializable(String ignored, int alsoIgnored) {
		}

	}

}

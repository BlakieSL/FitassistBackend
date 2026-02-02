package com.fitassist.backend.service.implementation.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;

@Service
public class JsonPatchServiceImpl implements JsonPatchService {

	private final ObjectMapper objectMapper;

	public JsonPatchServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public <T> T applyPatch(JsonMergePatch patch, Object targetBean, Class<T> beanClass)
			throws JsonPatchException, JsonProcessingException {
		JsonNode targetNode = objectMapper.valueToTree(targetBean);
		JsonNode patchedNode = patch.apply(targetNode);
		return objectMapper.treeToValue(patchedNode, beanClass);
	}

	@Override
	public <T> T createFromPatch(JsonMergePatch patch, Class<T> beanClass)
			throws JsonPatchException, JsonProcessingException {
		ObjectNode emptyNode = objectMapper.createObjectNode();
		JsonNode patchedNode = patch.apply(emptyNode);
		return objectMapper.treeToValue(patchedNode, beanClass);
	}

}

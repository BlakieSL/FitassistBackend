package source.code.unit.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;

@ExtendWith(MockitoExtension.class)
public class JsonPatchServiceTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private JsonPatchServiceImpl jsonPatchService;

	private JsonMergePatch patch;

	private Object targetBean;

	private JsonNode targetNode;

	private JsonNode patchedNode;

	private Class<Object> beanClass;

	@BeforeEach
	void setUp() {
		patch = mock(JsonMergePatch.class);
		targetBean = new Object();
		targetNode = mock(JsonNode.class);
		patchedNode = mock(JsonNode.class);
		beanClass = Object.class;
	}

	@Test
	void applyPatch_shouldApplyPatchAndReturnPatchedBean() throws JsonPatchException, JsonProcessingException {
		when(objectMapper.valueToTree(targetBean)).thenReturn(targetNode);
		when(patch.apply(targetNode)).thenReturn(patchedNode);
		when(objectMapper.treeToValue(patchedNode, beanClass)).thenReturn(targetBean);

		Object result = jsonPatchService.applyPatch(patch, targetBean, beanClass);

		assertEquals(targetBean, result);
		verify(objectMapper).valueToTree(targetBean);
		verify(patch).apply(targetNode);
		verify(objectMapper).treeToValue(patchedNode, beanClass);
	}

	@Test
	void applyPatch_shouldThrowJsonPatchException() throws JsonPatchException {
		when(objectMapper.valueToTree(targetBean)).thenReturn(targetNode);
		when(patch.apply(targetNode)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> jsonPatchService.applyPatch(patch, targetBean, beanClass));
	}

	@Test
	void applyPatch_shouldThrowJsonProcessingException() throws JsonPatchException, JsonProcessingException {
		when(objectMapper.valueToTree(targetBean)).thenReturn(targetNode);
		when(patch.apply(targetNode)).thenReturn(patchedNode);
		when(objectMapper.treeToValue(patchedNode, beanClass)).thenThrow(JsonProcessingException.class);

		assertThrows(JsonProcessingException.class, () -> jsonPatchService.applyPatch(patch, targetBean, beanClass));
	}

}

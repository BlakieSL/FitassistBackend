package source.code.service.Implementation.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import source.code.service.Declaration.Helpers.JsonPatchService;

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
}

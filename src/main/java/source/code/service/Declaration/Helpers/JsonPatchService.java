package source.code.service.Declaration.Helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

public interface JsonPatchService {
  <T> T applyPatch(JsonMergePatch patch, Object targetBean, Class<T> beanClass)
          throws JsonPatchException, JsonProcessingException;
}

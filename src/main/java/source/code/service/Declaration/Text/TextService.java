package source.code.service.Declaration.Text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Response.Text.BaseTextResponseDto;

import java.util.List;

public interface TextService {
  void deleteText(int id);
  void updateText(int id, JsonMergePatch patch)  throws JsonPatchException, JsonProcessingException;
  List<BaseTextResponseDto> getAllByParent(int exerciseId);
}

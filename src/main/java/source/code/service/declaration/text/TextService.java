package source.code.service.declaration.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;

import source.code.dto.response.text.BaseTextResponseDto;

public interface TextService {

	void deleteText(int id);

	void updateText(int id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	List<BaseTextResponseDto> getAllByParent(int exerciseId);

}

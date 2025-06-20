package source.code.service.declaration.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupUpdateDto;
import source.code.dto.response.workoutSetGroup.WorkoutSetGroupResponseDto;

public interface JsonPatchService {
    <T> T applyPatch(JsonMergePatch patch, Object targetBean, Class<T> beanClass)
            throws JsonPatchException, JsonProcessingException;
}
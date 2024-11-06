package source.code.service.declaration.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.response.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserCreateDto request);

    void deleteUser(int id);

    void updateUser(int userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    UserResponseDto getUser(int id);

    int getUserIdByEmail(String email);
}

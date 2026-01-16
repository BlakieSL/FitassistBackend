package com.fitassist.backend.service.declaration.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;

public interface UserService {

	UserResponseDto register(UserCreateDto request);

	void deleteUser(int id);

	void updateUser(int userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void updateUserSimple(int userId, UserUpdateDto updateDto);

	UserResponseDto getUser(int id);

	AuthorDto getPublicUser(int id);

	int getUserIdByEmail(String email);

}

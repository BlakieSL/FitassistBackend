package com.fitassist.backend.service.declaration.user;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import jakarta.json.JsonMergePatch;

public interface UserService {

	UserResponseDto register(UserCreateDto request);

	void deleteUser(int id);

	void updateUser(int userId, JsonMergePatch patch) throws JacksonException;

	void updateUserSimple(int userId, UserUpdateDto updateDto);

	UserResponseDto getUser(int id);

	AuthorDto getPublicUser(int id);

	int getUserIdByEmail(String email);

}

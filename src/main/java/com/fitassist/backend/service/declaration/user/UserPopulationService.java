package com.fitassist.backend.service.declaration.user;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;

public interface UserPopulationService {

	void populate(UserResponseDto user);

	void populate(AuthorDto author);

}

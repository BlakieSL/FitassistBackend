package com.fitassist.backend.integration.test.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.model.user.Gender;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.integration.utils.Utils;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@UserSql
	@Test
	@DisplayName("GET - /{id} - Should retrieve a user by id when owner")
	void getUser() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/users/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.username").value("user1"));
	}

	@UserSql
	@Test
	@DisplayName("GET - /{id} - Should retrieve a user by id when admin")
	void getUserAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		mockMvc.perform(get("/api/users/1"))
			.andExpectAll(status().isOk(), jsonPath("$.id").value(1), jsonPath("$.username").value("user1"));
	}

	@UserSql
	@Test
	@DisplayName("GET - /{id} - Should return 404 when not found")
	void getNonExistentUser() throws Exception {
		Utils.setAdminContext(2);

		mockMvc.perform(get("/api/users/999")).andExpect(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("GET - /{id} - Should return 403 when not owner or admin")
	void getUserForbidden() throws Exception {
		Utils.setUserContext(2);

		mockMvc.perform(get("/api/users/1")).andExpect(status().isForbidden());
	}

	@WithAnonymousUser
	@UserSql
	@Test
	@DisplayName("POST - /register - Should register a new user")
	void registerUser() throws Exception {
		var request = UserCreateDto.of("newUser", "newUser@gmail.com", "Dimas@123", Gender.FEMALE,
				LocalDate.of(2020, 1, 1));

		mockMvc
			.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpectAll(status().isCreated(), jsonPath("$.id").exists(), jsonPath("$.username").value("newUser"));
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should update user when owner")
	void updateUser() throws Exception {
		Utils.setUserContext(1);
		int id = 1;
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedUser1");

		mockMvc
			.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id))
			.andExpectAll(status().isOk(), jsonPath("$.username").value("updatedUser1"));
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should update user when admin")
	void updateUserAsAdmin() throws Exception {
		Utils.setAdminContext(2);

		int id = 1;
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setEmail("updated1@gmail.com");

		mockMvc
			.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id))
			.andExpectAll(status().isOk(), jsonPath("$.email").value("updated1@gmail.com"));
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should return 403 when not owner or admin")
	void updateUserForbidden() throws Exception {
		Utils.setUserContext(3);

		int id = 1;
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedUser1");

		mockMvc
			.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isForbidden());
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should return 404 when user not found")
	void updateUserNotFound() throws Exception {
		Utils.setAdminContext(1);

		int id = 999;
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedUser1");

		mockMvc
			.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should return 400 when invalid patch request")
	void updateUserInvalidPatch() throws Exception {
		Utils.setUserContext(1);

		int id = 1;
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedUser1updatedUser1updatedUser1updatedUser1");

		mockMvc
			.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isBadRequest());
	}

	@UserSql
	@Test
	@DisplayName("PATCH - /{id} - Should ignore invalid fields in patch")
	void updateUserIgnoreInvalidFields() throws Exception {
		Utils.setUserContext(1);

		int id = 1;
		String patch = """
				{
				    "invalidFieldName": "updatedUser1"
				}
				""";

		mockMvc.perform(patch("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON).content(patch))
			.andExpectAll(status().isNoContent());
	}

	@UserSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete user when owner")
	void deleteUser() throws Exception {
		Utils.setUserContext(1);
		int id = 1;

		mockMvc.perform(delete("/api/users/{id}", id)).andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id)).andExpect(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("DELETE - /{id} - Should delete user when admin")
	void deleteUserAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		int id = 1;

		mockMvc.perform(delete("/api/users/{id}", id)).andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id)).andExpect(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("DELETE - /{id} - Should return 403 when not owner or admin")
	void deleteUserForbidden() throws Exception {
		Utils.setUserContext(3);
		int id = 1;

		mockMvc.perform(get("/api/users/{id}", id)).andExpect(status().isForbidden());
	}

	@UserSql
	@Test
	@DisplayName("DELETE - /{id} - Should return 404 when user not found")
	void deleteUserNotFound() throws Exception {
		Utils.setAdminContext(1);
		int id = 999;

		mockMvc.perform(delete("/api/users/{id}", id)).andExpectAll(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should update user when owner")
	void updateUserSimple() throws Exception {
		Utils.setUserContext(1);
		int id = 1;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedUserPut");
		updateDto.setEmail("user1@gmail.com");
		updateDto.setGender(Gender.MALE);
		updateDto.setBirthday(LocalDate.of(2000, 1, 1));

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id))
			.andExpectAll(status().isOk(), jsonPath("$.username").value("updatedUserPut"));
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should update user when admin")
	void updateUserSimpleAsAdmin() throws Exception {
		Utils.setAdminContext(2);
		int id = 1;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("updatedByAdmin");
		updateDto.setEmail("adminupdated@gmail.com");
		updateDto.setGender(Gender.FEMALE);
		updateDto.setBirthday(LocalDate.of(1995, 5, 15));

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id))
			.andExpectAll(status().isOk(), jsonPath("$.username").value("updatedByAdmin"),
					jsonPath("$.email").value("adminupdated@gmail.com"));
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should return 403 when not owner or admin")
	void updateUserSimpleForbidden() throws Exception {
		Utils.setUserContext(3);
		int id = 1;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("shouldNotUpdate");

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isForbidden());
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should return 404 when user not found")
	void updateUserSimpleNotFound() throws Exception {
		Utils.setAdminContext(1);
		int id = 999;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("shouldNotUpdate");

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNotFound());
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should return 400 when validation fails")
	void updateUserSimpleInvalidData() throws Exception {
		Utils.setUserContext(1);
		int id = 1;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("thisUsernameIsWayTooLongAndShouldFailValidation");
		updateDto.setEmail("invalid-email");

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isBadRequest());
	}

	@UserSql
	@Test
	@DisplayName("PUT - /{id} - Should update only provided fields")
	void updateUserSimplePartialUpdate() throws Exception {
		Utils.setUserContext(1);
		int id = 1;

		var updateDto = new UserUpdateDto();
		updateDto.setUsername("partialUpdate");

		mockMvc
			.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpectAll(status().isNoContent());

		mockMvc.perform(get("/api/users/{id}", id))
			.andExpectAll(status().isOk(), jsonPath("$.username").value("partialUpdate"),
					jsonPath("$.email").value("user1@example.com"));
	}

}

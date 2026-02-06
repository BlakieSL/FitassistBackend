package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.UserCredentialsDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.model.user.Role;
import com.fitassist.backend.model.user.RoleEnum;
import com.fitassist.backend.model.user.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommonMappingHelper.class)
public abstract class UserMapper {

	@Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToNames")
	public abstract UserCredentialsDto toDetails(User user);

	@Mapping(target = "calculatedCalories", ignore = true)
	@Mapping(target = "userImageUrl", ignore = true)
	@Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToEnums")
	public abstract UserResponseDto toResponse(User user);

	@Mapping(source = "user", target = ".", qualifiedByName = "mapUserToAuthorDto")
	@Mapping(target = "imageUrl", ignore = true)
	@Mapping(target = "imageName", ignore = true)
	public abstract AuthorDto toAuthorDto(User user);

	@Mapping(target = "password", ignore = true)
	@Mapping(target = "username", source = "dto", qualifiedByName = "generateUsername")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCarts", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "userRecipes", ignore = true)
	@Mapping(target = "userExercises", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "writtenComments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "createdForumThreads", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	@Mapping(target = "recipes", ignore = true)
	@Mapping(target = "plans", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract User toEntity(UserCreateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCarts", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "userRecipes", ignore = true)
	@Mapping(target = "userExercises", ignore = true)
	@Mapping(target = "userPlans", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "userActivities", ignore = true)
	@Mapping(target = "userCommentLikes", ignore = true)
	@Mapping(target = "writtenComments", ignore = true)
	@Mapping(target = "userThreads", ignore = true)
	@Mapping(target = "createdForumThreads", ignore = true)
	@Mapping(target = "complaints", ignore = true)
	@Mapping(target = "recipes", ignore = true)
	@Mapping(target = "plans", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget User user, UserUpdateDto request);

	@Named("mapRolesToNames")
	Set<String> mapRolesToNames(Set<Role> roles) {
		return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
	}

	@Named("mapRolesToEnums")
	Set<RoleEnum> mapRolesToEnums(Set<Role> roles) {
		return roles.stream().map(Role::getName).collect(Collectors.toSet());
	}

	@Named("generateUsername")
	protected String generateUsername(UserCreateDto dto) {
		if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
			return dto.getUsername();
		}

		return dto.getEmail().substring(0, dto.getEmail().indexOf("@"));
	}

}

package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.pojo.UserCredentialsDto;
import com.fitassist.backend.dto.request.user.UserCreateDto;
import com.fitassist.backend.dto.request.user.UserUpdateDto;
import com.fitassist.backend.dto.response.user.UserResponseDto;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.model.user.Role;
import com.fitassist.backend.model.user.RoleEnum;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommonMappingHelper.class)
public abstract class UserMapper {

	@Autowired
	private MediaRepository mediaRepository;

	@Autowired
	private AwsS3Service s3Service;

	@Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRolesNames")
	public abstract UserCredentialsDto toDetails(User user);

	@Mapping(target = "calculatedCalories", ignore = true)
	@Mapping(target = "userImageUrl", expression = "java(getUserImageUrl(user))")
	@Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleEnums")
	public abstract UserResponseDto toResponse(User user);

	@Mapping(source = "user", target = ".", qualifiedByName = "userToAuthorDto")
	@Mapping(target = "imageUrl", expression = "java(getUserImageUrl(user))")
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
	public abstract void updateUserFromDto(@MappingTarget User user, UserUpdateDto request);

	@Named("rolesToRolesNames")
	Set<String> rolesToRolesNames(Set<Role> roles) {
		return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
	}

	@Named("rolesToRoleEnums")
	Set<RoleEnum> rolesToRoleEnums(Set<Role> roles) {
		return roles.stream().map(Role::getName).collect(Collectors.toSet());
	}

	@Named("generateUsername")
	protected String generateUsername(UserCreateDto dto) {
		if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
			return dto.getUsername();
		}

		return dto.getEmail().substring(0, dto.getEmail().indexOf("@"));
	}

	String getUserImageUrl(User user) {
		return mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(user.getId(), MediaConnectedEntity.USER)
			.map(media -> s3Service.getImage(media.getImageName()))
			.orElse(null);
	}

}

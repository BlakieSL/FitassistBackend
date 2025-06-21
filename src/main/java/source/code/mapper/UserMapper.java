package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import source.code.dto.pojo.UserCredentialsDto;
import source.code.dto.request.user.UserCreateDto;
import source.code.dto.request.user.UserUpdateDto;
import source.code.dto.response.user.UserResponseDto;
import source.code.model.user.Role;
import source.code.model.user.User;
import source.code.repository.RoleRepository;
import source.code.service.declaration.helpers.CalculationsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private CalculationsService calculationsService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRolesNames")
    public abstract UserCredentialsDto toDetails(User user);

    @Mapping(target = "calculatedCalories", expression = "java(calculatedCalories(user))")
    public abstract UserResponseDto toResponse(User user);

    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
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
    public abstract User toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
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
    public abstract void updateUserFromDto(@MappingTarget User user, UserUpdateDto request);

    public void addDefaultRole(User user) {
        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(role);
    }

    //helpers

    @Named("rolesToRolesNames")
    Set<String> rolesToRolesNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("hashPassword")
    protected String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    BigDecimal calculatedCalories(User user) {
        if (!hasRequiredData(user)) {
            return null;
        }
        int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
        return calculationsService.calculateCaloricNeeds(
                user.getWeight(),
                user.getHeight(),
                age,
                user.getGender(),
                user.getActivityLevel(),
                user.getGoal()
        );
    }

    private boolean hasRequiredData(User user) {
        return user.getWeight() != null
                && user.getHeight() != null
                && user.getActivityLevel() != null
                && user.getGoal() != null
                && user.getBirthday() != null
                && user.getGender() != null;
    }
}

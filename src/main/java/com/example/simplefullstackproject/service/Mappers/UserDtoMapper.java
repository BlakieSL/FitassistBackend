package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.UserAdditionDto;
import com.example.simplefullstackproject.dto.UserDto;
import com.example.simplefullstackproject.dto.UserResponse;
import com.example.simplefullstackproject.model.Role;
import com.example.simplefullstackproject.model.User;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDtoMapper {
    public static UserDto mapDetails(User user) {
        return new UserDto(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }

    public UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getGender(),
                user.getBirthday(),
                user.getHeight(),
                user.getWeight(),
                user.getCalculatedCalories(),
                user.getGoal(),
                user.getActivityLevel());
    }

    public User map(UserAdditionDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setGoal(request.getGoal());
        user.setActivityLevel(request.getActivityLevel());

        return user;
    }
}

package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.UserDto;
import com.example.simplefullstackproject.Dtos.UserRequest;
import com.example.simplefullstackproject.Dtos.UserResponse;
import com.example.simplefullstackproject.Models.Role;
import com.example.simplefullstackproject.Models.User;
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
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getGender(),
                user.getAge(),
                user.getHeight(),
                user.getWeight(),
                user.getCalculatedCalories());
    }

    public User map(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());

        return user;
    }
}

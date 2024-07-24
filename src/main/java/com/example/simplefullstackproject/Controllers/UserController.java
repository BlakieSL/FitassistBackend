package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.UserRequest;
import com.example.simplefullstackproject.Dtos.UserResponse;
import com.example.simplefullstackproject.Dtos.UserUpdateRequest;
import com.example.simplefullstackproject.Exceptions.ValidationException;
import com.example.simplefullstackproject.Services.UserExerciseService;
import com.example.simplefullstackproject.Services.UserPlanService;
import com.example.simplefullstackproject.Services.UserRecipeService;
import com.example.simplefullstackproject.Services.UserService;
import com.example.simplefullstackproject.Validations.ValidationGroups;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    private final UserService userService;
    private final UserRecipeService userRecipeService;
    private final UserExerciseService userExerciseService;
    private final UserPlanService userPlanService;
    public UserController(
            UserService userService,
            UserRecipeService userRecipeService,
            UserExerciseService userExerciseService,
            UserPlanService userPlanService) {
        this.userService = userService;
        this.userRecipeService = userRecipeService;
        this.userExerciseService = userExerciseService;
        this.userPlanService = userPlanService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> modifyUserById(@PathVariable Integer id,
                                               @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch, BindingResult bindingResult) throws JsonPatchException, JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        userService.modifyUser(id,patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/recipes/{recipeId}")
    public ResponseEntity<Void> addRecipeToUser(@PathVariable Integer userId, @PathVariable Integer recipeId) {
        userRecipeService.addRecipeToUser(recipeId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/recipes/{recipeId}")
    public ResponseEntity<Void> deleteRecipeFromUser(@PathVariable Integer userId, @PathVariable Integer recipeId) {
        userRecipeService.deleteRecipeFromUser(recipeId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/exercises/{exerciseId}/add")
    public ResponseEntity<Void> addExerciseToUser(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId) {
        userExerciseService.addExerciseToUser(exerciseId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/exercises/{exerciseId}/remove")
    public ResponseEntity<Void> deleteExerciseFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId) {
        userExerciseService.deleteExerciseFromUser(exerciseId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/plans/{planId}")
    public ResponseEntity<Void> addPlanToUser(
            @PathVariable Integer userId,
            @PathVariable Integer planId) {
        userPlanService.addPlanToUser(planId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/plans/{planId}")
    public ResponseEntity<Void> deletePlanFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer planId) {
        userPlanService.deletePlanFromUser(planId, userId);
        return ResponseEntity.ok().build();
    }
}

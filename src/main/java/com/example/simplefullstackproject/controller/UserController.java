package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.auth.JwtService;
import com.example.simplefullstackproject.dto.*;
import com.example.simplefullstackproject.exception.ValidationException;
import com.example.simplefullstackproject.service.*;
import com.example.simplefullstackproject.validation.ValidationGroups;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final UserFoodService userFoodService;
    private final UserActivityService userActivityService;
    private final JwtService jwtService;
    public UserController(
            UserService userService,
            UserRecipeService userRecipeService,
            UserExerciseService userExerciseService,
            UserPlanService userPlanService,
            UserFoodService userFoodService,
            UserActivityService userActivityService,
            JwtService jwtService) {
        this.userService = userService;
        this.userRecipeService = userRecipeService;
        this.userExerciseService = userExerciseService;
        this.userPlanService = userPlanService;
        this.userFoodService = userFoodService;
        this.userActivityService = userActivityService;
        this.jwtService = jwtService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDtoRequest dtoRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        String newAccessToken = jwtService.refreshAccessToken(dtoRequest.getRefreshToken());
        AccessTokenDtoRequest accessTokenDtoRequest = new AccessTokenDtoRequest(newAccessToken);
        return ResponseEntity.ok(accessTokenDtoRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserAdditionDto request, BindingResult bindingResult) {
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

    @PostMapping("/{userId}/exercises/{exerciseId}")
    public ResponseEntity<Void> addExerciseToUser(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId) {
        userExerciseService.addExerciseToUser(exerciseId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/exercises/{exerciseId}")
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

    @PostMapping("/{userId}/foods/{foodId}")
    public ResponseEntity<Void> addFoodToUser(
            @PathVariable Integer userId,
            @PathVariable Integer foodId) {
        userFoodService.addFoodToUser(foodId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/foods/{foodId}")
    public ResponseEntity<Void> deleteFoodFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer foodId) {
        userFoodService.deleteFoodFromUser(foodId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/activities/{activityId}")
    public ResponseEntity<Void> addActivityToUser(
            @PathVariable Integer userId,
            @PathVariable Integer activityId) {
        userActivityService.addActivityToUser(activityId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivityFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer activityId) {
        userActivityService.deleteActivityFromUser(activityId, userId);
        return ResponseEntity.ok().build();
    }

}

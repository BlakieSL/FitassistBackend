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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable int id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenDtoRequest dtoRequest,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult);
        }
        String newAccessToken = jwtService.refreshAccessToken(dtoRequest.getRefreshToken());
        AccessTokenDtoRequest accessTokenDtoRequest = new AccessTokenDtoRequest(newAccessToken);
        return ResponseEntity.ok(accessTokenDtoRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserAdditionDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> modifyUserById(
            @PathVariable int id,
            @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch,
            BindingResult bindingResult) throws JsonPatchException, JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        userService.modifyUser(id,patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/recipes/{recipeId}/type/{typeId}")
    public ResponseEntity<Void> saveRecipeToUser(
            @PathVariable int userId,
            @PathVariable int recipeId,
            @PathVariable short typeId
    ) {
        userRecipeService.saveRecipeToUser(recipeId, userId, typeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/recipes/{recipeId}/type/{typeId}")
    public ResponseEntity<Void> deleteSavedRecipeFromUser(
            @PathVariable int userId,
            @PathVariable int recipeId,
            @PathVariable short typeId
    ) {
        userRecipeService.deleteSavedRecipeFromUser(recipeId, userId, typeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/exercises/{exerciseId}/type/{typeId}")
    public ResponseEntity<Void> saveExerciseToUser(
            @PathVariable int userId,
            @PathVariable int exerciseId,
            @PathVariable short typeId
    ) {
        userExerciseService.saveExerciseToUser(exerciseId, userId, typeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/exercises/{exerciseId}/type/{typeId}")
    public ResponseEntity<Void> deleteSavedExerciseFromUser(
            @PathVariable int userId,
            @PathVariable int exerciseId,
            @PathVariable short typeId
    ) {
        userExerciseService.deleteSavedExerciseFromUser(exerciseId, userId, typeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/plans/{planId}/type/{typeId}")
    public ResponseEntity<Void> savePlanToUser(
            @PathVariable int userId,
            @PathVariable int planId,
            @PathVariable short typeId) {
        userPlanService.savePlanToUser(planId, userId, typeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/plans/{planId}/type/{typeId}")
    public ResponseEntity<Void> deleteSavedPlanFromUser(
            @PathVariable int userId,
            @PathVariable int planId,
            @PathVariable short typeId) {
        userPlanService.deleteSavedPlanFromUser(planId, userId, typeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/foods/{foodId}/type/{typeId}")
    public ResponseEntity<Void> saveFoodToUser(
            @PathVariable int userId,
            @PathVariable int foodId,
            @PathVariable short typeId
    ) {
        userFoodService.saveFoodToUser(foodId, userId, typeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/foods/{foodId}/type/{typeId}")
    public ResponseEntity<Void> deleteSavedFoodFromUser(
            @PathVariable int userId,
            @PathVariable int foodId,
            @PathVariable short typeId
    ) {
        userFoodService.deleteSavedFoodFromUser(foodId, userId, typeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/activities/{activityId}/type/{typeId}")
    public ResponseEntity<Void> saveActivityToUser(
            @PathVariable int userId,
            @PathVariable int activityId,
            @PathVariable short typeId
    ) {
        userActivityService.saveActivityToUser(activityId, userId, typeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/activities/{activityId}/type/{typeId}")
    public ResponseEntity<Void> deleteSavedActivityFromUser(
            @PathVariable int userId,
            @PathVariable int activityId,
            @PathVariable short typeId
    ) {
        userActivityService.deleteSavedActivityFromUser(activityId, userId, typeId);
        return ResponseEntity.ok().build();
    }
}

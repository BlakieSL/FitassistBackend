package com.example.simplefullstackproject.Controllers;

import com.example.simplefullstackproject.Dtos.UserRequest;
import com.example.simplefullstackproject.Dtos.UserResponse;
import com.example.simplefullstackproject.Dtos.UserUpdateRequest;
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
    private final ObjectMapper objectMapper;
    private final UserRecipeService userRecipeService;

    public UserController(UserService userService, ObjectMapper objectMapper, UserRecipeService userRecipeService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.userRecipeService = userRecipeService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Integer id, @Validated(ValidationGroups.Registration.class) @RequestBody JsonMergePatch patch, BindingResult bindingResult) throws JsonPatchException, JsonProcessingException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        UserResponse response = userService.getUserById(id);
        UserUpdateRequest request = applyPatch(response, patch);

        userService.updateUser(id, request);
        return ResponseEntity.noContent().build();
    }

    private UserUpdateRequest applyPatch(UserResponse response, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode userNode = objectMapper.valueToTree(response);
        JsonNode patchNode = patch.apply(userNode);
        return objectMapper.treeToValue(patchNode, UserUpdateRequest.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/recipes/{recipeId}")
    public ResponseEntity<?> addRecipeToUser(@PathVariable Integer userId, @PathVariable Integer recipeId) {
        userRecipeService.addRecipeToUser(recipeId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/recipes/{recipeId}")
    public ResponseEntity<?> deleteRecipeFromUser(@PathVariable Integer userId, @PathVariable Integer recipeId) {
        userRecipeService.deleteRecipeFromUser(recipeId, userId);
        return ResponseEntity.ok().build();
    }
}

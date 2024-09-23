package com.gdsc.studyjamapi.controller;

import com.gdsc.studyjamapi.api.UserApi;
import com.gdsc.studyjamapi.dto.request.CreateUserRequest;
import com.gdsc.studyjamapi.dto.request.EditUserRequest;
import com.gdsc.studyjamapi.dto.response.UserResponse;
import com.gdsc.studyjamapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
  private final UserService userService;

  @Override
  public ResponseEntity<UserResponse> createUser(
      CreateUserRequest request, UriComponentsBuilder ucb) {
    UserResponse newUser = userService.createUser(request);
    URI locationOfNewUser = ucb.path("/users/{id}").buildAndExpand(newUser.getId()).toUri();
    return ResponseEntity.created(locationOfNewUser).build();
  }

  public ResponseEntity<UserResponse> editUser(
          EditUserRequest request, UriComponentsBuilder ucb) {
    UserResponse editUser = userService.editUser(request);
    URI locationOfNewUser = ucb.path("/users/{id}").buildAndExpand(editUser.getId()).toUri();
    return ResponseEntity.created(locationOfNewUser).build();
  }

  @Override
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    var result = userService.getAllUsers();
    return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<UserResponse> getUserById(String userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @Override
  public ResponseEntity<UserResponse> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUserResponse());
  }
}

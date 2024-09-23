package com.gdsc.studyjamapi.service;

import com.gdsc.studyjamapi.common.Role;
import com.gdsc.studyjamapi.dto.request.CreateUserRequest;
import com.gdsc.studyjamapi.dto.request.EditUserRequest;
import com.gdsc.studyjamapi.dto.response.UserResponse;
import com.gdsc.studyjamapi.entity.User;
import com.gdsc.studyjamapi.exception.NotFoundException;
import com.gdsc.studyjamapi.exception.EmailAlreadyExistsException;
import com.gdsc.studyjamapi.mapper.UserMapper;
import com.gdsc.studyjamapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gdsc.studyjamapi.common.Constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponse createUser(CreateUserRequest request) {
    Optional<User> optionalUser = userRepository.findUserByEmail(request.getEmail());
    if (optionalUser.isPresent()) {
      throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);
    }

    User newUserRequest =
        User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.valueOf(request.getRole()))
            .isEnabled(true)
            .build();
    User newUser = userRepository.save(newUserRequest);

    return UserMapper.INSTANCE.userToUserResponse(newUser);
  }

  public List<UserResponse> getAllUsers() {
    List<User> users = userRepository.findAll();
    return UserMapper.INSTANCE.listUserToListUserResponse(users);
  }

  public UserResponse getUserById(String userId) {
    User user =
        userRepository
            .findById(UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_TEMPLATE.formatted("User")));
    return UserMapper.INSTANCE.userToUserResponse(user);
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    return userRepository.findUserByEmail(currentUserEmail).orElseThrow();
  }

  public UserResponse getCurrentUserResponse() {
    return UserMapper.INSTANCE.userToUserResponse(getCurrentUser());
  }

  public UserResponse editUser(EditUserRequest request) {
    // Check if the user exists by email
    Optional<User> optionalUser = userRepository.findUserByEmail(request.getEmail());
    if (!optionalUser.isPresent()) {
      System.err.println("User not found with email: " + request.getEmail());
      throw new NotFoundException(EMAIL_NOT_FOUND);
    }

    User user = optionalUser.get();
    System.out.println("Editing user with email: " + user.getEmail());

    // Update user details
    user.setFullName(request.getFullName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.valueOf(request.getRole()));

    // Save updated user
    User updatedUser = userRepository.save(user);
    System.out.println("User updated successfully: " + updatedUser.getId());

    // Map updated user to response
    return UserMapper.INSTANCE.userToUserResponse(updatedUser);
  }

}

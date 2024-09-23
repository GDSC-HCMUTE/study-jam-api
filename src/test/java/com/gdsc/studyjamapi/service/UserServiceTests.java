package com.gdsc.studyjamapi.service;

import com.gdsc.studyjamapi.common.Role;
import com.gdsc.studyjamapi.dto.request.CreateUserRequest;
import com.gdsc.studyjamapi.dto.request.EditUserRequest;
import com.gdsc.studyjamapi.dto.response.UserResponse;
import com.gdsc.studyjamapi.entity.User;
import com.gdsc.studyjamapi.exception.EmailAlreadyExistsException;
import com.gdsc.studyjamapi.exception.NotFoundException;
import com.gdsc.studyjamapi.mapper.UserMapper;
import com.gdsc.studyjamapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
  @MockBean private UserRepository userRepository;
  @MockBean private PasswordEncoder passwordEncoder;
  @Autowired private UserService userService;

  @Test
  void createUser_requestWithExistingEmail_throwsException() {
    String existingEmail = "existing@gmail.com";
    CreateUserRequest request =
        CreateUserRequest.builder()
            .fullName("Full Name")
            .email(existingEmail)
            .password("password")
            .role("ADMIN")
            .build();
    User existingUser =
        User.builder()
            .fullName("Another Full Name")
            .email(existingEmail)
            .password("encodedPassword")
            .role(Role.PARTICIPANT)
            .build();
    when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(existingUser));
    assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
  }

  @Test
  void createUser_giveSuitableRequest_returnOk() {
    CreateUserRequest request =
        CreateUserRequest.builder()
            .fullName("Full Name")
            .email("test@gmail.com")
            .password("password")
            .role("SUPER_ADMIN")
            .build();

    when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(Optional.empty());

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

    User newUserRequest =
        User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(encodedPassword)
            .role(Role.valueOf(request.getRole()))
            .isEnabled(true)
            .build();
    User expectedNewUser =
        User.builder()
            .id(UUID.fromString("7ce10103-45af-4569-90ea-213306d05ec1"))
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(encodedPassword)
            .role(Role.valueOf(request.getRole()))
            .isEnabled(true)
            .build();

    when(userRepository.save(newUserRequest)).thenReturn(expectedNewUser);
    UserResponse actualResponse = userService.createUser(request);

    assertNotNull(actualResponse);
    assertEquals(expectedNewUser.getId(), actualResponse.getId());
    assertEquals(expectedNewUser.getFullName(), actualResponse.getFullName());
    assertEquals(expectedNewUser.getEmail(), actualResponse.getEmail());
    assertEquals(expectedNewUser.getRole(), actualResponse.getRole());
    assertTrue(actualResponse.getIsEnabled());
  }

  @Test
  void getAllUsers_returnOk() {
    User userA =
        User.builder()
            .id(UUID.fromString("673b9ec1-580b-4a8f-8e31-b9228a908951"))
            .email("testA@gmail.com")
            .build();
    User userB =
        User.builder()
            .id(UUID.fromString("5753d691-8bb7-456f-940d-da08b9f070f8"))
            .email("testB@gmail.com")
            .build();
    List<User> expectedUserList = List.of(userA, userB);

    when(userRepository.findAll()).thenReturn(expectedUserList);
    List<UserResponse> actualResponse = userService.getAllUsers();

    assertNotNull(actualResponse);
    assertEquals(expectedUserList.size(), actualResponse.size());
    assertEquals(userA.getId(), actualResponse.get(0).getId());
    assertEquals(userB.getId(), actualResponse.get(1).getId());
    assertEquals(userA.getEmail(), actualResponse.get(0).getEmail());
    assertEquals(userB.getEmail(), actualResponse.get(1).getEmail());
  }

  @Test
  void getUserById_giveNonexistentId_throwsException() {
    String nonexistentId = "5753d691-8bb7-456f-940d-da08b9f070f8";
    when(userRepository.findById(UUID.fromString(nonexistentId))).thenReturn(Optional.empty());
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> userService.getUserById(nonexistentId));
    assertEquals(exception.getMessage(), "User not found");
  }

  @Test
  void getUserById_giveExistingId_returnOk() {
    String exisingId = "5753d691-8bb7-456f-940d-da08b9f070f8";
    User expecteUser =
        User.builder().id(UUID.fromString(exisingId)).email("test@gmail.com").build();

    when(userRepository.findById(UUID.fromString(exisingId))).thenReturn(Optional.of(expecteUser));
    UserResponse actualResponse = userService.getUserById(exisingId);

    assertNotNull(actualResponse);
    assertEquals(expecteUser.getId(), actualResponse.getId());
    assertEquals(expecteUser.getEmail(), actualResponse.getEmail());
  }

  @Test
  @WithMockUser(username = "mock-user@gmail.com")
  void getCurrentUserResponse_returnOk() {
    String mockUserEmail = "mock-user@gmail.com";
    User expecteUser =
        User.builder()
            .id(UUID.fromString("5753d691-8bb7-456f-940d-da08b9f070f8"))
            .email(mockUserEmail)
            .build();

    when(userRepository.findUserByEmail(mockUserEmail)).thenReturn(Optional.of(expecteUser));
    UserResponse actualResponse = userService.getCurrentUserResponse();

    assertNotNull(actualResponse);
    assertEquals(expecteUser.getId(), actualResponse.getId());
    assertEquals(expecteUser.getEmail(), actualResponse.getEmail());
  }

  @Test
  void CanCreateAndEditAccount() {
    CreateUserRequest request = CreateUserRequest.builder()
            .fullName("Full Name")
            .email("test@gmail.com")
            .password("password")
            .role("SUPER_ADMIN")
            .build();

    when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(Optional.empty());

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

    User newUserRequest =
            User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(encodedPassword)
                    .role(Role.valueOf(request.getRole()))
                    .isEnabled(true)
                    .build();
    User expectedNewUser =
            User.builder()
                    .id(UUID.fromString("7ce10103-45af-4569-90ea-213306d05ec1"))
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(encodedPassword)
                    .role(Role.valueOf(request.getRole()))
                    .isEnabled(true)
                    .build();

    when(userRepository.save(any(User.class))).thenReturn(expectedNewUser); // Use any() here
    UserResponse actualResponse = userService.createUser(request);

    when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(Optional.of(expectedNewUser));

    EditUserRequest editRequest =
            EditUserRequest.builder()
                    .fullName("New full name")
                    .email(request.getEmail())
                    .password("newPassword")
                    .role("SUPER_ADMIN")
                    .build();

    String newEncodedPassword = "newEncodedPassword"; // Ensure the encoded password is correct
    when(passwordEncoder.encode(editRequest.getPassword())).thenReturn(newEncodedPassword);

    User updatedUser = User.builder()
            .id(expectedNewUser.getId())
            .fullName(editRequest.getFullName())
            .email(editRequest.getEmail())
            .password(newEncodedPassword)
            .role(Role.valueOf(editRequest.getRole()))
            .isEnabled(true)
            .build();

    when(userRepository.save(any(User.class))).thenReturn(updatedUser); // Use any() here
    UserResponse editedUserResponse = userService.editUser(editRequest);

    assertNotNull(editedUserResponse);
    assertEquals(editRequest.getFullName(), editedUserResponse.getFullName());
    assertEquals(editRequest.getEmail(), editedUserResponse.getEmail());
    assertTrue(actualResponse.getIsEnabled());
  }


  @Test
  void testUserToUserResponseMapping() {
    User user = User.builder()
            .id(UUID.fromString("7ce10103-45af-4569-90ea-213306d05ec1"))
            .fullName("New full name")
            .email("test@gmail.com")
            .password("encodedPassword")
            .role(Role.SUPER_ADMIN)
            .isEnabled(true)
            .build();

    UserResponse response = UserMapper.INSTANCE.userToUserResponse(user);
    assertNotNull(response);
    assertEquals(user.getFullName(), response.getFullName());
  }
}

package com.gdsc.studyjamapi.api;

import com.gdsc.studyjamapi.dto.request.CreateUserRequest;
import com.gdsc.studyjamapi.dto.request.EditUserRequest;
import com.gdsc.studyjamapi.dto.response.ErrorResponse;
import com.gdsc.studyjamapi.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequestMapping("/users")
@Tag(name = "User")
public interface UserApi {
  @Operation(summary = "Create a new user. This API is for SUPER_ADMIN only.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema)}),
        @ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))
            })
      })
  @PostMapping
  ResponseEntity<UserResponse> createUser(
      @RequestBody @Valid CreateUserRequest request, UriComponentsBuilder ucb);

  @Operation(summary = "Get all users. This API is not for PARTICIPANT role.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Got a list of all users",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = List.class))
            }),
      })
  @GetMapping
  ResponseEntity<List<UserResponse>> getAllUsers();

  @Operation(summary = "Get a user by ID. This API is not for PARTICIPANT role.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Got a user by the supplied ID",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @GetMapping("/{userId}")
  ResponseEntity<UserResponse> getUserById(
      @Parameter(description = "ID of the user to get") @PathVariable String userId);

  @Operation(summary = "Get the information of the logged in user.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Got the logged in user",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserResponse.class))
            }),
      })
  @GetMapping("/current")
  ResponseEntity<UserResponse> getCurrentUser();
  @PutMapping("/{id}")
    ResponseEntity<UserResponse> editUser(
            @RequestBody @Valid EditUserRequest request, UriComponentsBuilder ucb
  );

}

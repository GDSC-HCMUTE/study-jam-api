package com.gdsc.studyjamapi.dto.request;

import com.gdsc.studyjamapi.validation.constraint.ValidRoleConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.gdsc.studyjamapi.common.Constants.ErrorMessage.*;
import static com.gdsc.studyjamapi.common.Constants.ErrorMessage.REQUIRED_ROLE;
import static com.gdsc.studyjamapi.common.Constants.PASSWORD_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {
    @NotBlank(message = REQUIRED_FULL_NAME)
    private String fullName;

    @NotBlank(message = REQUIRED_EMAIL)
    private String email;

    @NotBlank(message = REQUIRED_PASSWORD)
    @Pattern(regexp = PASSWORD_PATTERN, message = WEAK_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotBlank(message = REQUIRED_ROLE)
    @ValidRoleConstraint
    private String role;
}

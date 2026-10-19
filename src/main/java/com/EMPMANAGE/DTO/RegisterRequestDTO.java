package com.EMPMANAGE.DTO;

import com.EMPMANAGE.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String userOfficialEmail;

    @NotBlank
    private String password;

    // Public registration will always assign EMPLOYEE; this field is not trusted.
    private Role role;
}

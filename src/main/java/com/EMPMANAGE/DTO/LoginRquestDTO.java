package com.EMPMANAGE.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRquestDTO {
    @NotBlank
    @Email
    private String userOfficialEmail;

    @NotBlank
    private String password;
}

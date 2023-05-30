package com.springboot.evaluation_task.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "UserName Invalid")
    private String email;
    @NotBlank(message = "Password Invalid")
    private String password;
}

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
public class UserRequest {

    @NotBlank(message = "UserName Invalid")
    private String userName;
    @NotBlank(message = "Password Invalid")
    private String password;
}

package com.springboot.evaluation_task.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SourceTokenRequest {
    @NotBlank(message = "Token Should Not Empty")
    private String token;
}

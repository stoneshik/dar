package com.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {
    @NotEmpty
    @Size(min = 2, max = 15)
    @JsonProperty("login")
    private String login;
    @NotEmpty
    @Size(min = 5)
    @JsonProperty("password")
    private String password;
}

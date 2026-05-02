package com.whitetower.meridia.dto;

import com.whitetower.meridia.annotation.Password;
import com.whitetower.meridia.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDTO{
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Password()
    private String password;


    public User toEntity(){
        return new User(-1L, name, email, password, 0);
    }
}

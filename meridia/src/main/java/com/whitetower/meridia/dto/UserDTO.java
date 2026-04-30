package com.whitetower.meridia.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull(message = "Size availability cannot be null")
    @PositiveOrZero(message = "Size must be 0 or greater")
    private Integer sizeAvailable;
}

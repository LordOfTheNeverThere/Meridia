package com.whitetower.meridia.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhotoDTO {

    @NotBlank(message = "Data is required")
    private byte[] data;

    @NotBlank(message = "Name is required")
    private String name;
}

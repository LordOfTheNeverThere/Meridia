package com.whitetower.meridia.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class ResponseDTO<T> {
    @NonNull
    private T payload;
    private List<String> errors;
}

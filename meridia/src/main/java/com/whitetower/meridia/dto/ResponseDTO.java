package com.whitetower.meridia.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class ResponseDTO<T> {
    private T payload;
    private List<String> errors;

    public ResponseDTO(T newPayload) {
        payload = newPayload;
    }

    public ResponseDTO(T newPayload, List<String> newErrors) {
        payload = newPayload;
        errors = newErrors;
    }
}

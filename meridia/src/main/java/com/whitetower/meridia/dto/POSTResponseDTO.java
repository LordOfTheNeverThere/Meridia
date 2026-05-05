package com.whitetower.meridia.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.NonNull;


public class POSTResponseDTO extends ResponseDTO<Long> {
    public POSTResponseDTO(@NonNull Long payload) {
        super(payload);
    }
}

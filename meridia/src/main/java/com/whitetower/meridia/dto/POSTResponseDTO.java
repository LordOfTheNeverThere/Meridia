package com.whitetower.meridia.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class POSTResponseDTO extends ResponseDTO {

    @NonNull
    private Long id;
}

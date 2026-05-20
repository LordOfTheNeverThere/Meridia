package com.whitetower.meridia.dto;

import java.util.List;


public class RetrievablePOSTResponseDTO extends ResponseDTO<Long> {
    public RetrievablePOSTResponseDTO(Long payload) {
        super(payload);
    }

    public RetrievablePOSTResponseDTO(Long payload, List<String> errors) {
        super(payload, errors);
    }
}

package com.whitetower.meridia.service;

import com.whitetower.meridia.enumeration.ServiceResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceResponse<T> {

    protected ServiceResponseType type;
    protected T value;
}

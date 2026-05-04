package com.whitetower.meridia.controller;


import com.whitetower.meridia.dto.POSTResponseDTO;
import com.whitetower.meridia.dto.UserRegistrationDTO;
import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.service.ServiceResponse;
import com.whitetower.meridia.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserController {

    final static public String API_URI_PREFIX = "/api/v1";
    final static public String API_USER_POST = API_URI_PREFIX + "/user";

    @Autowired
    private UserService service;

    @PostMapping(API_USER_POST)
    public ResponseEntity<POSTResponseDTO> newUser(@Valid @RequestBody UserRegistrationDTO dto){

        ServiceResponse<Long> serviceResponse;
        try {
            serviceResponse = service.createUser(dto.toEntity());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.ENTITY_IS_INVALID,-1L);
        } catch (Exception e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.UNKNOWN_DB_ERROR,-1L);
        }


        if (serviceResponse.getType() == ServiceResponseType.OK){
            return new ResponseEntity<>(new POSTResponseDTO(serviceResponse.getValue()),HttpStatus.CREATED);
        } else {
            POSTResponseDTO badRequestDTO = new POSTResponseDTO(-1L);
            badRequestDTO.setErrors(List.of(serviceResponse.getType().message));
            return new ResponseEntity<>(badRequestDTO,HttpStatus.BAD_REQUEST);
        }
    }
}

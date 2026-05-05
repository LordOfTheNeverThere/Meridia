package com.whitetower.meridia.controller;


import com.whitetower.meridia.dto.POSTResponseDTO;
import com.whitetower.meridia.dto.ResponseDTO;
import com.whitetower.meridia.dto.UserLoginDTO;
import com.whitetower.meridia.dto.UserRegistrationDTO;
import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.service.ServiceResponse;
import com.whitetower.meridia.service.UserService;
import com.whitetower.meridia.util.Security;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;


@RestController
public class UserController {

    final static public String API_URI_PREFIX = "/api/v1";
    final static public String API_USER_POST = API_URI_PREFIX + "/user";
    final static public String API_SIGN_IN = API_URI_PREFIX + "/sign-in";

    @Autowired
    private UserService service;

    @Autowired
    private Security security;

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

    @PostMapping(API_SIGN_IN)
    public ResponseEntity<POSTResponseDTO> signIn(@Valid @RequestBody UserLoginDTO dto){
        ServiceResponse<String> serviceResponse;

        try {
            serviceResponse = service.validateUser(dto.toEntity());
        } catch (Exception e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.UNKNOWN_DB_ERROR,"");
        }

        if (serviceResponse.getType() == ServiceResponseType.OK){

            ResponseCookie cookie = ResponseCookie.from("jwt-token", serviceResponse.getValue()) // Set cookie value as the token
                    .httpOnly(true)          // Prevents JavaScript access
                    .secure(true)            // Only sent over HTTPS
                    .path("/")               // Available for all routes
                    .maxAge(security.getJwtExpirationMs()/1000) // Expiry in seconds
                    .sameSite("Strict")      // Prevents CSRF
                    .build();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
            return new ResponseEntity<>(headers ,HttpStatus.OK);

        } else if (serviceResponse.getType() == ServiceResponseType.ENTITY_NOT_FOUND) {
            POSTResponseDTO notFoundDTO = new POSTResponseDTO(-1L);
            notFoundDTO.setErrors(List.of(serviceResponse.getType().message));
            return new ResponseEntity<>(notFoundDTO, HttpStatus.NOT_FOUND);

        } else  {
            POSTResponseDTO badRequestDTO = new POSTResponseDTO(-1L);
            badRequestDTO.setErrors(List.of(serviceResponse.getType().message));
            return new ResponseEntity<>(badRequestDTO,HttpStatus.BAD_REQUEST);
        }
    }
}

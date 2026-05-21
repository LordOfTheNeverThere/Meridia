package com.whitetower.meridia.controller;


import com.whitetower.meridia.dto.*;
import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.service.ServiceResponse;
import com.whitetower.meridia.service.UserService;
import com.whitetower.meridia.util.Security;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
public class UserController {

    final static public String API_URI_PREFIX = "/api/v1";
    final static public String API_USER = API_URI_PREFIX + "/user";
    final static public String API_LOGIN = API_URI_PREFIX + "/login";

    @Autowired
    private UserService service;

    @Autowired
    private Security security;

    public static @NonNull HttpHeaders wwwAuthenticateHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"Meridia\""); // Sends WWW-Authenticate header as per RFC standard
        return headers;
    }


    @PostMapping(API_USER)
    public ResponseEntity<ResponseDTO<UserDTO>> newUser(@Valid @RequestBody UserRegistrationDTO dto){

        ServiceResponse<UserDTO> serviceResponse;
        try {
            serviceResponse = service.createUser(dto.toEntity());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.ENTITY_IS_INVALID,null);
        } catch (Exception e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.UNKNOWN_DB_ERROR,null);
        }


        if (serviceResponse.getType() == ServiceResponseType.OK){ // No point in returning the object or
            return new ResponseEntity<>(new ResponseDTO<>(serviceResponse.getValue()), HttpStatus.CREATED);
        } else {
            ResponseDTO<UserDTO> badRequestDTO = new ResponseDTO<>(serviceResponse.getValue(), List.of(serviceResponse.getType().message));
            return new ResponseEntity<>(badRequestDTO,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(API_LOGIN)
    public ResponseEntity<RetrievablePOSTResponseDTO> login(@Valid @RequestBody UserLoginDTO dto){
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

        } else  {
            RetrievablePOSTResponseDTO badRequestDTO = new RetrievablePOSTResponseDTO(null, List.of(serviceResponse.getType().message));
            return new ResponseEntity<>(badRequestDTO, wwwAuthenticateHeader(), HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(API_USER)
    public ResponseEntity<UserDTO> delete(@CookieValue("jwt-token") String jwt){

        if ( jwt == null || jwt.isEmpty()) return new ResponseEntity<>((UserDTO) null, wwwAuthenticateHeader(), HttpStatus.UNAUTHORIZED);

        Optional<Long> optID = security.validateJweGetSubject(jwt);
        if (optID.isEmpty()) return new ResponseEntity<>((UserDTO) null, wwwAuthenticateHeader(), HttpStatus.FORBIDDEN);

        ServiceResponse<UserDTO> serviceResponse;
        try {
            serviceResponse = service.delete(optID.get());
        } catch (Exception e) {
            serviceResponse = new ServiceResponse<>(ServiceResponseType.UNKNOWN_DB_ERROR,null);
        }

        if (serviceResponse.getType() == ServiceResponseType.OK) {
            return new ResponseEntity<>(serviceResponse.getValue(), HttpStatus.OK);
        } else if (serviceResponse.getType() == ServiceResponseType.ENTITY_NOT_FOUND) {
            return new ResponseEntity<>(serviceResponse.getValue(), HttpStatus.NOT_FOUND);
        } else  {
            return new ResponseEntity<>(serviceResponse.getValue(), HttpStatus.BAD_REQUEST);
        }
    }
}

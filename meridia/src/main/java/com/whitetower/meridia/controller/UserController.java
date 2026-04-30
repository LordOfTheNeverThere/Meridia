package com.whitetower.meridia.controller;


import com.whitetower.meridia.dto.POSTResponseDTO;
import com.whitetower.meridia.dto.UserDTO;
import com.whitetower.meridia.dto.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    final static public String API_URI_PREFIX = "/api/v1";
    final static public String API_USER_POST = API_URI_PREFIX + "/user";



    @PostMapping(API_USER_POST)
    public ResponseEntity<POSTResponseDTO> newUser(@Valid @RequestBody UserRegistrationDTO dto){
        return new ResponseEntity<POSTResponseDTO>(new POSTResponseDTO(1L),HttpStatus.CREATED);
    }
}

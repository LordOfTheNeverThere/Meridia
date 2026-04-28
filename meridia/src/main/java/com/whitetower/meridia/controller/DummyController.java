package com.whitetower.meridia.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @GetMapping("/helloworld")
    public String helloWorld(){
        return "hello world";
    }
}

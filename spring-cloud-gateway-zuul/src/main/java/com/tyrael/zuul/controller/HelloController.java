package com.tyrael.zuul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        String name = "tyrael";
        return "Hello " +name+" consul 2";
    }
}

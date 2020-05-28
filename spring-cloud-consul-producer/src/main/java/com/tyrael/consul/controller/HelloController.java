package com.tyrael.consul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(@RequestParam String name) {

        return "Hello " + name + " consul 1";
    }

    @RequestMapping("/hellofeign")
    public String hellofeign() {
        return "Hello " + " feign consul 1";
    }
}

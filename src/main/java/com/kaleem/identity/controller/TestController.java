package com.kaleem.identity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        String s = "Public endpoint";
        return s;
    }

    @GetMapping("/secure")
    public String secureEndpoint(Authentication authentication) {
        return "Hello " + authentication.getName();
    }
}
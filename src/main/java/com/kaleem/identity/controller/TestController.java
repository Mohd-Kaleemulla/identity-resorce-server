package com.kaleem.identity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        String s = "Public endpoint";
        return s;
    }



    @GetMapping("/secure")
    public Map<String, Object> secureEndpoint(@AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        response.put("roles", realmAccess);
        response.put("subject", jwt.getSubject());
        response.put("username", jwt.getClaim("preferred_username"));
        response.put("email", jwt.getClaim("email"));
        response.put("issuer", jwt.getIssuer());

        return response;
    }
}
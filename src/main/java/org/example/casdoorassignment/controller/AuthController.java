package org.example.casdoorassignment.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.example.casdoorassignment.CasdoorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
public class AuthController {
    private final CasdoorProperties openIdConnectProperties;
    private final RestTemplate restTemplate;

    private final String redirectUri = "https://localhost:7443/callback";

    @Autowired
    public AuthController(CasdoorProperties openIdConnectProperties, RestTemplate restTemplate) {
        this.openIdConnectProperties = openIdConnectProperties;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        System.out.println("Login requested");

        String authorizeUrl = openIdConnectProperties.getEndpoint() + "/login/oauth/authorize" +
                "?client_id=" + openIdConnectProperties.getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=openid profile email";
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        System.out.println("Got code: " + code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("client_id", openIdConnectProperties.getClientId());
        formData.add("client_secret", openIdConnectProperties.getClientSecret());
        formData.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        String tokenEndpointUrl = openIdConnectProperties.getEndpoint() + "/api/login/oauth/access_token";
        System.out.println("Requesting token from: " + tokenEndpointUrl);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                openIdConnectProperties.getEndpoint() + "/api/login/oauth/access_token",
                request,
                Map.class);
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Set-Cookie", "access_token=" + accessToken + "; Path=/");

        response.sendRedirect("/");
    }
}

package org.example.casdoorassignment.controller;

import io.jsonwebtoken.Claims;

import org.example.casdoorassignment.JwtDecoder;
import org.example.casdoorassignment.controller.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class UserController {

 
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;

    @Autowired
    public UserController(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            log.warn("Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Authorization header is missing"));
        }
        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Authorization header does not start with Bearer schema");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Invalid authorization schema. Expected Bearer token."));
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            log.warn("Token is empty after removing Bearer prefix");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Token is missing"));
        }
        log.debug("Received token for userinfo endpoint (first 20 chars): {}", token.substring(0, Math.min(token.length(), 20)));
        try {
            Claims claims = jwtDecoder.decodeToken(token);
            log.debug("Successfully decoded claims: {}", claims);

            String userId = claims.getSubject();
            String username = Objects.requireNonNullElse(claims.get("name", String.class), "N/A");

            if (!StringUtils.hasText(userId)) {
                log.error("Token decoded successfully but 'subject' (userId) claim is missing or empty. Claims: {}", claims);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("User identifier missing in token."));
            }
            UserInfoDTO userInfo = new UserInfoDTO(userId, username);
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) { 
            log.error("Token validation/decoding failed: {}", e.getMessage(), e); 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Invalid or expired token. " + e.getMessage()));
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing userinfo request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An unexpected error occurred."));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);
        return errorDetails;
    }

    @Value("${app.websocket.url}")
    private String websocketUrl;

    @GetMapping("/config")
    public Map<String, String> getAppConfig() {
        return Map.of("websocketUrl", websocketUrl);
    }
}
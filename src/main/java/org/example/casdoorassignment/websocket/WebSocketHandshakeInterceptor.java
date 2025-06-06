package org.example.casdoorassignment.websocket;

import io.jsonwebtoken.Claims;
import org.example.casdoorassignment.JwtDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);
  private final JwtDecoder jwtDecoder;

  @Autowired
  public WebSocketHandshakeInterceptor(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    String token = null;
    // Extract token from query parameter (e.g., ws://localhost:7443/coins?token=YOUR_JWT_TOKEN)
    String query = request.getURI().getQuery();
    if (query != null) {
      Map<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().toSingleValueMap();
      token = queryParams.get("token");
    }

    if (token != null) {
      try {
        Claims claims = jwtDecoder.decodeToken(token);
        String userId = claims.getSubject(); // Or whatever claim identifies the user
        attributes.put("userId", userId); // Store userId for later use in WebSocketSession
        logger.info("WebSocket handshake approved for user: {}", userId);
        return true; // Allow handshake
      } catch (Exception e) {
        logger.warn("Invalid token for WebSocket handshake: {}", e.getMessage());
        // Optionally set HTTP status on response before returning false,
        // though client might not always see it for WebSocket upgrade.
        // response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // Deny handshake
      }
    }
    logger.warn("No token provided for WebSocket handshake.");
    return false; // Deny handshake if no token
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
  }
}

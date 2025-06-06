package org.example.casdoorassignment.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final UserWebSocketHandler userWebSocketHandler;
  private final WebSocketHandshakeInterceptor handshakeInterceptor;

  @Autowired
  public WebSocketConfig(UserWebSocketHandler userWebSocketHandler, WebSocketHandshakeInterceptor handshakeInterceptor) {
    this.userWebSocketHandler = userWebSocketHandler;
    this.handshakeInterceptor = handshakeInterceptor;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(userWebSocketHandler, "/coins") // Your WebSocket endpoint
        .addInterceptors(handshakeInterceptor)
        .setAllowedOrigins("*"); // Configure allowed origins appropriately for production
  }
}

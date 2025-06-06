package org.example.casdoorassignment.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class UserWebSocketHandler extends TextWebSocketHandler {

  private static final Logger logger = LoggerFactory.getLogger(UserWebSocketHandler.class);
  private final UserSessionStore userSessionStore;

  public UserWebSocketHandler(UserSessionStore userSessionStore) {
    this.userSessionStore = userSessionStore;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String userId = (String) session.getAttributes().get("userId"); // Set by HandshakeInterceptor
    if (userId != null) {
      logger.info("User WebSocket connection established: Session ID = {}, User ID = {}", session.getId(), userId);
      userSessionStore.addSession(session, userId);
    } else {
      logger.warn("User WebSocket connection established without userId, closing: {}", session.getId());
      session.close(CloseStatus.POLICY_VIOLATION.withReason("Unauthorized: Missing or invalid token"));
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    // For this lab, we primarily broadcast, not receive from client on this channel
    logger.info("Received message from user {}: {}", session.getAttributes().get("userId"), message.getPayload());
    // session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    logger.info("User WebSocket connection closed: {} - Status: {}", session.getId(), status);
    userSessionStore.removeSession(session);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.error("User WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
    userSessionStore.removeSession(session); // Clean up on error
  }
}

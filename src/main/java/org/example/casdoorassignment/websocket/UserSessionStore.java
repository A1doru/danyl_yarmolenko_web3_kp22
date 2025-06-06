package org.example.casdoorassignment.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionStore {
  private static final Logger logger = LoggerFactory.getLogger(UserSessionStore.class);
  // Map<SessionId, WebSocketSession>
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
  // You might also want to map userId to session if a user can have only one session

  public void addSession(WebSocketSession session, String userId) {
    sessions.put(session.getId(), session);
    logger.info("Added session: {} for user: {}", session.getId(), userId);
  }

  public void removeSession(WebSocketSession session) {
    if (session != null) {
      sessions.remove(session.getId());
      logger.info("Removed session: {}", session.getId());
    }
  }

  public Map<String, WebSocketSession> getAllSessions() {
    return new ConcurrentHashMap<>(sessions); // Return a copy to avoid concurrent modification issues if iterating
  }

  public void broadcast(String message) {
    sessions.values().forEach(session -> {
      try {
        if (session.isOpen()) {
          session.sendMessage(new org.springframework.web.socket.TextMessage(message));
        }
      } catch (IOException e) {
        logger.error("Error broadcasting message to session {}: {}", session.getId(), e.getMessage());
      }
    });
  }
}

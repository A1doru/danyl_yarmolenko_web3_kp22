package org.example.casdoorassignment.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BinanceTickerMessageHandler extends TextWebSocketHandler {

  private static final Logger logger = LoggerFactory.getLogger(BinanceTickerMessageHandler.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final List<BinanceTickerUpdateListener> listeners = new ArrayList<>();

  public void addListener(BinanceTickerUpdateListener listener) {
    listeners.add(listener);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    logger.info("Connected to Binance WebSocket: {}", session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    try {
      String payload = message.getPayload();
      logger.debug("Received from Binance: {}", payload);
      BinanceWebSocketMessage binanceMessage = objectMapper.readValue(payload, BinanceWebSocketMessage.class);

      if (binanceMessage != null && binanceMessage.getData() != null) {
        for (BinanceTickerUpdateListener listener : listeners) {
          listener.onUpdate(binanceMessage);
        }
      }
    } catch (IOException e) {
      logger.error("Error processing Binance message: {}", e.getMessage());
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.error("Binance WebSocket transport error: {}", exception.getMessage());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
    logger.info("Disconnected from Binance WebSocket: {} - Status: {}", session.getId(), status);
    // You might want to implement reconnection logic here
  }
}

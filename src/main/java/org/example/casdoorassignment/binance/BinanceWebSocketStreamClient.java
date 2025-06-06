package org.example.casdoorassignment.binance;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.casdoorassignment.tickers.TickersUpdateForwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class BinanceWebSocketStreamClient {

  private static final Logger logger = LoggerFactory.getLogger(BinanceWebSocketStreamClient.class);

  @Value("${binance.websocket.base-url}")
  private String binanceBaseUrl;

  @Value("${binance.websocket.stream-path}")
  private String binanceStreamPath;

  private final BinanceTickerMessageHandler messageHandler;
  private final TickersUpdateForwarder tickersUpdateForwarder;
  private WebSocketSession binanceSession;

  public BinanceWebSocketStreamClient(BinanceTickerMessageHandler messageHandler,
                                      TickersUpdateForwarder tickersUpdateForwarder) {
    this.messageHandler = messageHandler;
    this.tickersUpdateForwarder = tickersUpdateForwarder;
  }

  @PostConstruct
  public void connect() {
    this.messageHandler.addListener(this.tickersUpdateForwarder);
    try {
      WebSocketClient client = new StandardWebSocketClient();
      String url = binanceBaseUrl + binanceStreamPath;
      logger.info("Connecting to Binance WebSocket: {}", url);
      this.binanceSession = client.execute(messageHandler, url).get();
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Failed to connect to Binance WebSocket: {}", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  @PreDestroy
  public void disconnect() {
    if (binanceSession != null && binanceSession.isOpen()) {
      try {
        logger.info("Disconnecting from Binance WebSocket.");
        binanceSession.close();
      } catch (IOException e) {
        logger.error("Error disconnecting from Binance WebSocket: {}", e.getMessage());
      }
    }
  }
}

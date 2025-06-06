package org.example.casdoorassignment.tickers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.casdoorassignment.binance.BinanceTickerData;
import org.example.casdoorassignment.binance.BinanceTickerUpdateListener;
import org.example.casdoorassignment.binance.BinanceWebSocketMessage;
import org.example.casdoorassignment.controller.dto.Coin;
import org.example.casdoorassignment.controller.dto.TickerUpdate;
import org.example.casdoorassignment.websocket.UserSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TickersUpdateForwarder implements BinanceTickerUpdateListener {

  private static final Logger logger = LoggerFactory.getLogger(TickersUpdateForwarder.class);
  private final UserSessionStore userSessionStore;
  private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON serialization

  public TickersUpdateForwarder(UserSessionStore userSessionStore) {
    this.userSessionStore = userSessionStore;
  }

  @Override
  public void onUpdate(BinanceWebSocketMessage message) {
    TickerUpdate tickerUpdate = toTickerUpdate(message);
    if (tickerUpdate != null) {
      try {
        String jsonUpdate = objectMapper.writeValueAsString(tickerUpdate);
        logger.debug("Forwarding update: {}", jsonUpdate);
        userSessionStore.broadcast(jsonUpdate);
      } catch (JsonProcessingException e) {
        logger.error("Error serializing TickerUpdate to JSON: {}", e.getMessage());
      }
    }
  }

  private TickerUpdate toTickerUpdate(BinanceWebSocketMessage message) {
    BinanceTickerData data = message.getData();
    if (data == null || data.getSymbol() == null || data.getLastPrice() == null) {
      return null;
    }

    Coin coin = Coin.fromSymbol(data.getSymbol());
    if (coin == null) {
      logger.warn("Unknown coin symbol: {}", data.getSymbol());
      return null;
    }

    try {
      double price = Double.parseDouble(data.getLastPrice());
      return new TickerUpdate(coin, data.getEventTime(), price);
    } catch (NumberFormatException e) {
      logger.error("Could not parse price for symbol {}: {}", data.getSymbol(), data.getLastPrice());
      return null;
    }
  }
}

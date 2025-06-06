package org.example.casdoorassignment.binance;

public interface BinanceTickerUpdateListener {
  void onUpdate(BinanceWebSocketMessage message);
}
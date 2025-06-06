package org.example.casdoorassignment.controller.dto;

public enum Coin {
  BTC("btcusdt"), ETH("ethusdt"), XRP("xrpusdt"), DOGE("dogeusdt"); // Match Binance symbol parts

  private final String tag;
  Coin(String tag) { this.tag = tag; }
  public String getTag() { return tag; }

  public static Coin fromSymbol(String symbol) {
    for (Coin coin : values()) {
      // Binance symbols are like "BTCUSDT", we look for "btcusdt"
      if (symbol.toLowerCase().startsWith(coin.getTag())) {
        return coin;
      }
    }
    return null; // Or throw an exception
  }
}

package org.example.casdoorassignment.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceTickerData {
  @JsonProperty("e")
  private String eventType;

  @JsonProperty("E")
  private long eventTime;

  @JsonProperty("s")
  private String symbol;

  @JsonProperty("p")
  private String priceChange;

  @JsonProperty("P")
  private String priceChangePercent;

  @JsonProperty("w")
  private String weightedAvgPrice;

  @JsonProperty("c")
  private String lastPrice;

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public long getEventTime() {
    return eventTime;
  }

  public void setEventTime(long eventTime) {
    this.eventTime = eventTime;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getLastPrice() {
    return lastPrice;
  }

  public void setLastPrice(String lastPrice) {
    this.lastPrice = lastPrice;
  }

  @Override
  public String toString() {
    return "BinanceTickerData{" +
        "eventType='" + eventType + '\'' +
        ", eventTime=" + eventTime +
        ", symbol='" + symbol + '\'' +
        ", lastPrice='" + lastPrice + '\'' +
        // ...
        '}';
  }
}
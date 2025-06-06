package org.example.casdoorassignment.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceWebSocketMessage {
  @JsonProperty("stream")
  private String stream;

  @JsonProperty("data")
  private BinanceTickerData data;

  public String getStream() {
    return stream;
  }

  public void setStream(String stream) {
    this.stream = stream;
  }

  public BinanceTickerData getData() {
    return data;
  }

  public void setData(BinanceTickerData data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "BinanceWebSocketMessage{" +
        "stream='" + stream + '\'' +
        ", data=" + (data != null ? data.toString() : "null") +
        '}';
  }
}

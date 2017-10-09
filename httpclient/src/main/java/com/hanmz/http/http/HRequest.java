package com.hanmz.http.http;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Data
public class HRequest {
  private String url;
  private Map<String, String> headers;
  private HttpBuffer buffer;
  private HttpClient client;

  public static Builder builder() {
    return new Builder();
  }

  public HttpClient getClient() {
    return client;
  }

  public void setClient(HttpClient client) {
    this.client = client;
  }


  public static class Builder {
    private String url;
    private Map<String, String> headers = Maps.newHashMap();

    public Builder addHeader(String name, String value) {
      headers.put(name, value);
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public HRequest build() {
      HRequest request = new HRequest();
      request.url = url;
      request.headers = headers;
      request.buffer = new HttpBuffer();
      return request;
    }
  }
}

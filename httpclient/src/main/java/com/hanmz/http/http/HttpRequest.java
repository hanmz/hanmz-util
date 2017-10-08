package com.hanmz.http.http;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Data
public class HttpRequest {
  private String url;
  private Map<String, String> headers;

  public static Builder builder() {
    return new Builder();
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

    public HttpRequest build() {
      HttpRequest request = new HttpRequest();
      request.url = url;
      request.headers = headers;
      return request;
    }
  }
}

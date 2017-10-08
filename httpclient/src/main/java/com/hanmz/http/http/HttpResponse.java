package com.hanmz.http.http;

import com.google.common.collect.Multimap;
import lombok.Data;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Data
public class HttpResponse {
  private String statusLine;
  private Multimap<String, String> headers;
  private String bodyString;


}

package com.hanmz.http;

import com.google.common.util.concurrent.Uninterruptibles;
import com.hanmz.http.http.HRequest;
import com.hanmz.http.http.HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class App {
  public static void main(String[] args) {
    HttpClient httpClient = new HttpClient();

    HRequest request = HRequest.builder()
                               .url("http://www.baidu.com/")
                               .addHeader("Connection", "Keep-Alive")
                               .addHeader("Accept-Encoding", "gzip")
                               .addHeader("test", "test")
                               .build();

    get(httpClient, request);
    out.println("*********************");

    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    get(httpClient, request);
    out.println("*********************");

  }

  private static void get(HttpClient client, HRequest request) {
    client.call(request);
  }

}

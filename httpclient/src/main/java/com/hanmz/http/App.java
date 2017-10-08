package com.hanmz.http;

import com.google.common.util.concurrent.Uninterruptibles;
import com.hanmz.http.http.HttpBuffer;
import com.hanmz.http.http.HttpClient;
import com.hanmz.http.http.HttpRequest;
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

    HttpRequest request = HttpRequest.builder()
                                     .url("http://www.baidu.com/")
                                     .addHeader("Connection", "Keep-Alive")
                                     .addHeader("Accept-Encoding", "gzip")
                                     .addHeader("test", "test")
                                     .build();
    HttpBuffer buffer = new HttpBuffer();

    get(httpClient, request, buffer);
    out.println("*********************");

    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

    get(httpClient, request, buffer);
    out.println("*********************");

  }

  private static void get(HttpClient client, HttpRequest request, HttpBuffer buffer) {
      new Retry(client, request, buffer).exec();
  }

}

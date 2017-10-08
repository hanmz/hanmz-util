package com.hanmz.http;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Uninterruptibles;
import com.hanmz.http.http.HttpBody;
import com.hanmz.http.http.HttpBuffer;
import com.hanmz.http.http.HttpClient;
import com.hanmz.http.http.HttpHeader;
import com.hanmz.http.http.HttpRequest;
import com.hanmz.http.http.HttpResponse;

import java.util.concurrent.TimeUnit;

import static com.hanmz.http.util.Utils.printfMap;
import static java.lang.System.out;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
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

  private static void get(HttpClient httpClient, HttpRequest request, HttpBuffer buffer) {
    httpClient.get(request, buffer);

    HttpHeader header = new HttpHeader(buffer);

    String statusLine = header.getStatusLine();
    HttpResponse response = new HttpResponse();
    response.setStatusLine(statusLine);
    out.println(statusLine);

    Multimap<String, String> headers = header.getHeaders();
    response.setHeaders(headers);
    out.println(printfMap(headers));

    HttpBody body = new HttpBody(buffer);
    //    out.println(headers.get("Content-Length").toArray()[0].toString());
    String bodyString = body.getBody(headers);
    response.setBodyString(bodyString);
    out.println(bodyString);
  }
}

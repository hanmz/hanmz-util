package com.hanmz.http;

import com.google.common.collect.Multimap;
import com.hanmz.http.exception.HttpException;
import com.hanmz.http.http.HttpBody;
import com.hanmz.http.http.HttpBuffer;
import com.hanmz.http.http.HttpClient;
import com.hanmz.http.http.HttpHeader;
import com.hanmz.http.http.HttpRequest;
import com.hanmz.http.http.HttpResponse;
import com.hanmz.http.util.RetryUtils;

import java.util.concurrent.Callable;

import static com.hanmz.http.util.Utils.printfMap;
import static java.lang.System.out;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public class Retry implements Callable<Object> {

  private HttpClient client;
  private HttpRequest request;
  private HttpBuffer buffer;

  public Retry(HttpClient client, HttpRequest request, HttpBuffer buffer) {
    this.client = client;
    this.request = request;
    this.buffer = buffer;

  }

  public void exec() {
    try {
      RetryUtils.executeWithRetry(this, 3, 100, false);
    }catch (Exception e){
      throw HttpException.asHttpException(e);
    }
  }

  @Override
  public Object call() {
    try {
      client.get(request, buffer);

      HttpHeader header = new HttpHeader(buffer);

      String statusLine = header.getStatusLine();
      HttpResponse response = new HttpResponse();
      response.setStatusLine(statusLine);
      out.println(statusLine);

      Multimap<String, String> headers = header.getHeader();
      response.setHeaders(headers);
      out.println(printfMap(headers));

      HttpBody body = new HttpBody(buffer, header);
      String bodyString = body.getStringBody();
      response.setBodyString(bodyString);
      out.println(bodyString);

      return null;
    } catch (Exception e) {
      client.cleanSocket(request);
      throw HttpException.asHttpException(e);
    }
  }
}

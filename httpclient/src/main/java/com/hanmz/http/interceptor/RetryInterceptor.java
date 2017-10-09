package com.hanmz.http.interceptor;

import com.google.common.collect.Multimap;
import com.hanmz.http.exception.HttpException;
import com.hanmz.http.http.HRequest;
import com.hanmz.http.http.HResponse;
import com.hanmz.http.http.HttpBody;
import com.hanmz.http.http.HttpClient;
import com.hanmz.http.http.HttpHeader;
import com.hanmz.http.util.RetryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Callable;

import static com.hanmz.http.util.Utils.printfMap;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
@Slf4j
public class RetryInterceptor implements HInterceptor {

  @Override
  public HResponse intercept(HChain chain) throws IOException {
    HRequest request = chain.request();

    new Retry(request).exec();

    return null;
  }

  private class Retry implements Callable<HResponse> {

    private HttpClient client;
    private HRequest request;

    Retry(HRequest request) {
      this.client = request.getClient();
      this.request = request;
    }

    void exec() {
      try {
        RetryUtils.executeWithRetry(this, 3, 100, false);
      } catch (Exception e) {
        throw HttpException.asHttpException(e);
      }
    }

    @Override
    public HResponse call() {
      try {
        client.get(request);

        HttpHeader header = new HttpHeader(request.getBuffer());

        String statusLine = header.getStatusLine();
        HResponse response = new HResponse();
        response.setStatusLine(statusLine);
        log.info("status line\n{}", statusLine);

        Multimap<String, String> headers = header.getHeader();
        response.setHeaders(headers);
        log.info("headers\n{}", printfMap(headers));

        HttpBody body = new HttpBody(request.getBuffer(), header);
        String bodyString = body.getStringBody();
        response.setBodyString(bodyString);
        log.info("response body\n{}", bodyString);

        return response;
      } catch (Exception e) {
        client.cleanSocket(request);
        throw HttpException.asHttpException(e);
      }
    }
  }
}

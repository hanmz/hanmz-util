package com.hanmz.http.interceptor;

import com.hanmz.http.http.HRequest;
import com.hanmz.http.http.HResponse;

import java.io.IOException;
import java.util.List;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
public class HRealChain implements HInterceptor.HChain {

  private HRequest request;
  private List<HInterceptor> interceptors;
  private int index;

  public HRealChain(HRequest request, List<HInterceptor> interceptors) {
    this.request = request;
    this.interceptors = interceptors;

  }

  @Override
  public HRequest request() {
    return request;
  }

  @Override
  public HResponse proceed(HRequest request) throws IOException {
    HResponse response = null;
    if (index < interceptors.size()) {
      HInterceptor interceptor = interceptors.get(index++);
      response = interceptor.intercept(this);
    }
    return response;
  }
}

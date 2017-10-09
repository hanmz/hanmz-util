package com.hanmz.http.interceptor;

import com.hanmz.http.http.HRequest;
import com.hanmz.http.http.HResponse;

import java.io.IOException;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
public interface HInterceptor {
  HResponse intercept(HInterceptor.HChain chain) throws IOException;

  interface HChain {
    HRequest request();

    HResponse proceed(HRequest request) throws IOException;

  }
}

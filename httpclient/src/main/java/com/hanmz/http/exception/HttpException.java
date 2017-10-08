package com.hanmz.http.exception;

import com.hanmz.http.util.FormatStringUtils;

import static com.hanmz.http.util.FormatStringUtils.formatString;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
public class HttpException extends RuntimeException {

  private HttpException(String errorMessage) {
    super(errorMessage);
  }

  private HttpException(String errorMessage, Throwable cause) {
    super(FormatStringUtils.getMessage(errorMessage) + " - " + FormatStringUtils.getMessage(cause), cause);
  }

  public static HttpException asHttpException(String errorMessage) {
    return new HttpException(errorMessage);
  }

  public static HttpException asHttpException(String message, Throwable cause) {
    if (cause instanceof HttpException) {
      return (HttpException) cause;
    }
    return new HttpException(message, cause);
  }

  public static HttpException asHttpException(String message, Object... objects) {
    return new HttpException(formatString(message, objects));
  }


  public static HttpException asHttpException(Throwable cause) {
    if (cause instanceof HttpException) {
      return (HttpException) cause;
    }
    return new HttpException(FormatStringUtils.getMessage(cause), cause);
  }
}

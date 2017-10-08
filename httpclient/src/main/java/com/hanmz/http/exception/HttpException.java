package com.hanmz.http.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
public class HttpException extends RuntimeException {

  private HttpException(String errorMessage) {
    super(errorMessage);
  }

  private HttpException(String errorMessage, Throwable cause) {
    super(getMessage(errorMessage) + " - " + getMessage(cause), cause);
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


  public static HttpException asHttpException(Throwable cause) {
    if (cause instanceof HttpException) {
      return (HttpException) cause;
    }
    return new HttpException(getMessage(cause), cause);
  }

  private static String getMessage(Object obj) {
    if (obj == null) {
      return "";
    }

    if (obj instanceof Throwable) {
      StringWriter str = new StringWriter();
      PrintWriter pw = new PrintWriter(str);
      ((Throwable) obj).printStackTrace(pw);
      return str.toString();
    }
    return obj.toString();
  }
}

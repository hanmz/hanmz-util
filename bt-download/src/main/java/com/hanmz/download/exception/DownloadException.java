package com.hanmz.download.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
public class DownloadException extends RuntimeException {

  private DownloadException(String errorMessage) {
    super(errorMessage);
  }

  private DownloadException(String errorMessage, Throwable cause) {
    super(getMessage(errorMessage) + " - " + getMessage(cause), cause);
  }

  public static DownloadException asDownloadException(String errorMessage) {
    return new DownloadException(errorMessage);
  }

  public static DownloadException asDownloadException(String message, Throwable cause) {
    if (cause instanceof DownloadException) {
      return (DownloadException) cause;
    }
    return new DownloadException(message, cause);
  }


  public static DownloadException asDownloadException(Throwable cause) {
    if (cause instanceof DownloadException) {
      return (DownloadException) cause;
    }
    return new DownloadException(getMessage(cause), cause);
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

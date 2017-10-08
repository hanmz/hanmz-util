package com.hanmz.http.util;

import com.google.common.collect.Multimap;
import com.hanmz.http.exception.HttpException;
import lombok.experimental.UtilityClass;

import java.net.Socket;
import java.nio.charset.Charset;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
@UtilityClass
public class Utils {

  public static final Charset UTF_8 = Charset.forName("UTF-8");

  public String getHost(String url) {
    int start = url.indexOf("//");
    int end;
    if (start < 0) {
      end = url.indexOf('/');
    } else {
      start += 2;
      end = url.indexOf('/', start + 2);
    }
    if (end < 0) {
      end = url.length();
    }
    return url.substring(start, end);
  }

  public String getUri(String url, String host) {
    int start = url.indexOf(host) + host.length();
    int end = url.indexOf('?', start);
    if (end < 0) {
      return url.substring(start);
    } else {
      return url.substring(start, end);
    }
  }

  private int pos(String str, int start, String con) {
    int pos = str.indexOf(con, start);
    if (pos < 0) {
      throw HttpException.asHttpException(String.format("not found %s from %s", con, str));
    }
    return pos;
  }

  /**
   * Closes {@code socket}, ignoring any checked exceptions. Does nothing if {@code socket} is
   * null.
   */
  public static void closeQuietly(Socket socket) {
    if (socket != null) {
      try {
        socket.close();
      } catch (AssertionError e) {
        if (!isAndroidGetsocknameError(e)) {
          throw e;
        }
      } catch (RuntimeException rethrown) {
        throw rethrown;
      } catch (Exception ignored) {
      }
    }
  }

  /**
   * Returns true if {@code e} is due to a firmware bug fixed after Android 4.2.2.
   * https://code.google.com/p/android/issues/detail?id=54072
   */
  public static boolean isAndroidGetsocknameError(AssertionError e) {
    return e.getCause() != null && e.getMessage() != null && e.getMessage().contains("getsockname failed");
  }

  public static String printfMap(Multimap<String, String> map) {
    StringBuilder sb = new StringBuilder(64);
    map.forEach((k, v) -> sb.append(k).append(" = ").append(v).append("\n"));
    return sb.toString();
  }
}

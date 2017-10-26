package com.hanmz.common.util;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.hanmz.download.exception.DownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class UrlEncodeUtils {
  private static final Charset UTF_8 = Charset.forName("UTF-8");

  public static String getFileName(URL url, String disposition) {

    int pos = disposition.indexOf("filename=");
    if (disposition.contains("attachment") && pos > 0) {
      return urlDecode(disposition.substring(pos + 9).trim());
    }

    String path = url.getPath();
    if (Strings.isNullOrEmpty(path)) {
      List<String> list = Splitter.on("/").trimResults().splitToList(path);
      if (!list.isEmpty()) {
        return list.get(list.size() - 1);
      }
    }

    return "tmp";
  }

  public static String urlDecode(String filename) {
    try {
      return URLDecoder.decode(filename, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw DownloadException.asDownloadException(e);
    }
  }
}

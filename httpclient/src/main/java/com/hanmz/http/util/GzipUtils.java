package com.hanmz.http.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Slf4j
public class GzipUtils {
  public static byte[] uncompress(byte[] bytes,int offset,int length) {
    if (bytes == null || bytes.length == 0) {
      return new byte[0];
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes,offset,length);
    try {
      GZIPInputStream ungzip = new GZIPInputStream(in);
      byte[] buffer = new byte[256];
      int n;
      while ((n = ungzip.read(buffer)) >= 0) {
        out.write(buffer, 0, n);
      }
    } catch (IOException e) {
      log.error("gzip uncompress error.", e);
    }

    return out.toByteArray();
  }
}

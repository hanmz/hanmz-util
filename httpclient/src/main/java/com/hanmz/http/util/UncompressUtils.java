package com.hanmz.http.util;

import com.hanmz.http.exception.HttpException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Slf4j
public class UncompressUtils {
  private static final int BUFFER_SIZE = 8096;

  private UncompressUtils() {
  }

  /**
   * 解压gzip压缩文件
   */
  public static byte[] gzip(byte[] bytes, int offset, int length) {
    if (bytes == null || bytes.length == 0) {
      return new byte[0];
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ByteArrayInputStream in = new ByteArrayInputStream(bytes, offset, length)) {
      GZIPInputStream ungzip = new GZIPInputStream(in);
      byte[] buffer = new byte[BUFFER_SIZE];
      int n;
      while ((n = ungzip.read(buffer)) >= 0) {
        out.write(buffer, 0, n);
      }
      return out.toByteArray();
    } catch (IOException e) {
      log.error("gzip uncompress error.", e);
      throw HttpException.asHttpException(e);
    }
  }

  /**
   * 解压deflate压缩文件
   */
  public static byte[] deflate(byte[] bytes, int offset, int length) {
    if (bytes == null || bytes.length == 0) {
      return new byte[0];
    }

    Inflater inflater = new Inflater();
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      inflater.setInput(bytes, offset, length);

      byte[] buffer = new byte[BUFFER_SIZE];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        out.write(buffer, 0, count);
      }
      return out.toByteArray();

    } catch (Exception e) {
      log.error("deflate uncompress error.", e);
      throw HttpException.asHttpException(e);
    }
  }
}

package com.hanmz.download.util;

import lombok.extern.slf4j.Slf4j;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class InnerUtils {
  /**
   * 转换Double为2位小数String
   */
  private static String format2(Object value) {
    if (null == value) {
      return "0";
    }
    if (value instanceof Number) {
      return d2s("%.02f", value);
    }
    return null;
  }

  private static String d2s(String format, Object val) {
    try {
      return String.format(format, (Double) val);
    } catch (Exception e) {
      log.error("cannot format double, val={}", val, e);
      return "0";
    }
  }

  public static String fileSize(long start, long end) {
    return fileSize((double) end - start);
  }

  public static String fileSize(double length) {

    if (length < 1024) {
      return format2(length) + "B";
    }
    length = length / 1024;
    if (length < 1024) {
      return format2(length) + "KB";
    }
    length = length / 1024;
    if (length < 1024) {
      return format2(length) + "MB";
    }
    length = length / 1024;
    if (length < 1024) {
      return format2(length) + "GB";
    }
    length = length / 1024;
    if (length < 1024) {
      return format2(length) + "TB";
    }
    return format2(length) + "PB";
  }
}

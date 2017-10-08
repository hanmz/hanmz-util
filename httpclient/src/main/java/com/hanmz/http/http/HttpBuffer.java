package com.hanmz.http.http;

import com.hanmz.http.util.UncompressUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

import static com.hanmz.http.http.HttpBody.ContentEncoding.COMPRESS;
import static com.hanmz.http.http.HttpBody.ContentEncoding.DEFLATE;
import static com.hanmz.http.http.HttpBody.ContentEncoding.GZIP;
import static com.hanmz.http.util.Utils.UTF_8;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class HttpBuffer {
  /**
   * socket 输出流
   */
  public OutputStream out;
  /**
   * socket 输入流
   */
  public InputStream in;
  /**
   * 缓存区大小
   */
  public int size = 65536;
  /**
   * 当前使用buffer
   */
  public int curSize;
  /**
   * 当前位置
   */
  public int pos;
  /**
   * 缓存区
   */
  public byte[] buffer = new byte[size];

  /**
   * ***************
   * response header
   */
  public int getPos(byte b) {
    int n;
    while ((n = containByte(b)) == -1) {
      fill();
    }
    return n;
  }

  private int containByte(byte b) {
    for (int i = pos; i < curSize; i++) {
      if (buffer[i] == b) {
        return i;
      }
    }
    return -1;
  }

  private int fill() {
    try {
      // len要小于当前剩余空间
      int n = in.read(buffer, curSize, size - curSize);
      if (n == -1) {
        return -1;
      }
      curSize += n;
      return n;
    } catch (Exception e) {
      log.error("", e);
    }
    return -1;
  }

  /**
   * **************
   * response body
   */

  public void initBody() {
    curSize -= pos;
    System.arraycopy(buffer, pos, buffer, 0, curSize);
    pos = 0;
    curSize = 0;
  }

  void refill(int length) {
    while (curSize < length) {
      fill();
    }
  }

  void init() {
    buffer = new byte[size];
    curSize = 0;
    pos = 0;
  }

  /**
   * 获取指点范围内容
   */
  String getString(int i) {
    String result = new String(buffer, pos, i - pos, UTF_8).trim();
    pos = i + 1;
    return result;
  }

  /**
   * 获取块内容，可能需要解压文件
   */
  String getTrunkedString(int length, String contentEncoding) {

    String res;
    if (GZIP.val.equalsIgnoreCase(contentEncoding)) {
      res = new String(UncompressUtils.gzip(buffer, pos, length), UTF_8);
    } else if (COMPRESS.val.equalsIgnoreCase(contentEncoding)) {
      res = new String(UncompressUtils.gzip(buffer, pos, length), UTF_8);
    } else if (DEFLATE.val.equalsIgnoreCase(contentEncoding)) {
      res = new String(UncompressUtils.deflate(buffer, pos, length), UTF_8);
    } else {
      res = new String(buffer, pos, length, UTF_8);
    }
    // 加2是因为后面还有\r\n
    pos += (length + 2);
    return res;

  }

}

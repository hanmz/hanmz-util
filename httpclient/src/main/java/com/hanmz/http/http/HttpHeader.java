package com.hanmz.http.http;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hanmz.http.util.InnerUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Collection;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class HttpHeader {

  private Multimap<String, String> header;

  private HttpBuffer buffer;

  public HttpHeader(HttpBuffer buffer) {
    this.buffer = buffer;
  }

  /**
   * 获取状态行
   */
  public String getStatusLine() {
    return getString(buffer.getPos((byte) '\n'));
  }

  public Multimap<String, String> getHeader() {
    if (header == null) {
      header = getHeaders();
    }
    return header;
  }

  /**
   * 获取请求头
   * 为什么用Multimap，因为可能存在单key多value的header，例如Set-Cookie
   */
  private Multimap<String, String> getHeaders() {
    Multimap<String, String> headers = ArrayListMultimap.create();
    String line;
    while ((line = getString(buffer.getPos((byte) '\n'))).length() != 0) {
      int i = line.indexOf(':');
      headers.put(line.substring(0, i).trim(), line.substring(i + 1).trim());
    }
    return headers;
  }

  private String getString(int i) {
    String result = new String(buffer.buffer, buffer.pos, i - buffer.pos, Charset.forName("UTF-8")).trim();
    buffer.pos = i + 1;
    return result;
  }

  public String getValue(String name, String defaultValue) {
    String value = getValue(name);
    return Strings.isNullOrEmpty(value) ? defaultValue : value;
  }

  public String getValue(String name) {
    Collection<String> values = header.get(name);
    return InnerUtils.isNullOrEmpty(values) ? null : values.toArray()[0].toString();
  }


}

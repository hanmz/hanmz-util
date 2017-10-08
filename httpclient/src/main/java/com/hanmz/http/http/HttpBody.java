package com.hanmz.http.http;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.hanmz.http.exception.HttpException;
import com.hanmz.http.util.InnerUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static com.hanmz.http.util.Utils.UTF_8;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
public class HttpBody {
  private HttpBuffer buffer;

  public HttpBody(HttpBuffer buffer) {
    this.buffer = buffer;
  }


  public String getBody(Multimap<String, String> headers) {
    if (!InnerUtils.isNullOrEmpty(headers.get("Content-Length"))) {
      int length = NumberUtils.toInt(headers.get("Content-Length").toArray()[0].toString());
      return getBody(length);
    }
    if (!InnerUtils.isNullOrEmpty(headers.get("Transfer-Encoding")) && "chunked".equalsIgnoreCase(headers.get("Transfer-Encoding").toArray()[0].toString())) {
      return getBody();
    }
    throw HttpException.asHttpException("Unsupported operations");
  }

  /**
   * content-length
   * 固定长度
   */
  private String getBody(int length) {
    buffer.initBody();
    buffer.refill(length);

    return new String(buffer.buffer, buffer.pos, buffer.curSize, UTF_8);
  }

  private String getBody() {
    buffer.initBody();

    StringBuilder sb = new StringBuilder();

    while (true) {
      String trunkLength = buffer.getString(buffer.getPos((byte) '\n')).trim();
      if (!Strings.isNullOrEmpty(trunkLength)) {
        int len = Integer.parseUnsignedInt(trunkLength, 16);
        if (len <= 0) {
          break;
        }
        buffer.refill(len);
        sb.append(buffer.getTrunkedString(len));
      }
    }

    return sb.toString();
  }

}

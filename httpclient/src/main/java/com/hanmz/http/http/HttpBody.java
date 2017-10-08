package com.hanmz.http.http;

import com.google.common.base.Strings;
import com.hanmz.http.exception.HttpException;
import com.hanmz.http.util.Constant;
import com.hanmz.http.util.InnerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import static com.hanmz.http.http.HttpBody.ContentEncoding.COMPRESS;
import static com.hanmz.http.http.HttpBody.ContentEncoding.DEFLATE;
import static com.hanmz.http.http.HttpBody.ContentEncoding.GZIP;
import static com.hanmz.http.http.HttpBody.ContentEncoding.IDENTITY;
import static com.hanmz.http.util.Constant.CONTENT_LENGTH;
import static com.hanmz.http.util.Constant.TRANSFER_ENCODING;
import static com.hanmz.http.util.Utils.UTF_8;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class HttpBody {
  private HttpBuffer buffer;
  private HttpHeader header;
  private String contentEncoding;

  public HttpBody(HttpBuffer buffer, HttpHeader header) {
    this.header = header;
    this.buffer = buffer;
  }


  public String getStringBody() {
    // 固定长度 response body
    if (!InnerUtils.isNullOrEmpty(header.getValue(CONTENT_LENGTH))) {
      log.info("content-length : {}", header.getValue(CONTENT_LENGTH));
      int length = NumberUtils.toInt(header.getValue(CONTENT_LENGTH));
      return getBody(length);
    }

    // 分块传输
    if (!InnerUtils.isNullOrEmpty(header.getValue(TRANSFER_ENCODING)) && "chunked".equalsIgnoreCase(header.getValue(TRANSFER_ENCODING))) {
      String value = header.getValue(Constant.CONTENT_ENCODING);
      if (Strings.isNullOrEmpty(value)) {
        value = IDENTITY.val;
        log.warn("Content-Encoding not found");
      }
      if (!isSupportEncoding(value)) {
        throw HttpException.asHttpException("Content-Encoding {} is unsupported", value);
      }
      contentEncoding = value;
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

  /**
   * 分块传输
   */
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
        sb.append(buffer.getTrunkedString(len, contentEncoding));
      }
    }

    return sb.toString();
  }

  private boolean isSupportEncoding(String contentEncoding) {
    return GZIP.val.equalsIgnoreCase(contentEncoding) || COMPRESS.val.equalsIgnoreCase(contentEncoding) || DEFLATE.val.equalsIgnoreCase(contentEncoding) ||
      IDENTITY.val.equalsIgnoreCase(contentEncoding);
  }

  public enum ContentEncoding {

    GZIP("gzip"), COMPRESS("compress"), DEFLATE("deflate"), IDENTITY("identity");
    String val;

    ContentEncoding(String val) {
      this.val = val;
    }


  }

}

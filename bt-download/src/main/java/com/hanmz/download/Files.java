package com.hanmz.download;

import lombok.Data;

import java.util.List;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
@Data
class Files {
  /**
   * 文件长度，总字节数
   */
  private long length;
  /**
   * 一个utf-8编码的字符串数组，最后一个字符串保存真实的文件名，前面的字符串保存文件路径。长度为0表示path字段不合法。
   */
  private List<String> path;
  private String md5sum;
}

package com.hanmz.download;

import lombok.Data;

import java.util.List;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
@Data
class Info {
  /**
   * 通常用作torrent文件的文件名
   */
  private String name;
  /**
   * 每一个peace（文件块）的字节长度
   */
  private long length;
  /**
   * 它的长度是20的倍数，每一段20个字符表示对应文件块的sha1 hash值
   */
  private byte[] pieces;
  private long piecesLength;
  private String md5sum;
  private List<Files> files;
}

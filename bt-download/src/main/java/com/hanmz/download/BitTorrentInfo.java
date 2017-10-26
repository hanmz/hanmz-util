package com.hanmz.download;

import com.google.common.collect.Lists;
import com.hanmz.download.exception.DownloadException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * metainfo files
 * <p>
 * 包含两部分：
 * announce ： bt tracker服务器地址
 * info ： info又是一个dictionaries（bencoding支持数据类型的嵌套），info里面的字符串都是使用utf-8编码。
 * <p>
 * Created by hanmz on 2017/10/9.
 */
@Data
public class BitTorrentInfo {
  static final List<String> KEY_LIST = Lists.newArrayList("announce", "announce-list", "creation date", "comment", "created by", "info", "length", "md5sum", "name", "piece length", "pieces", "files", "path");

  /**
   * bt tracker服务器地址
   */
  private String announce;
  private List<String> announceList;
  private long creationDate;
  private String comment;
  private String createBy;
  /**
   * info字典
   */
  private Info info;

  BitTorrentInfo() {
  }

  public BitTorrentInfo(String announce, List<String> announceList, long creationDate, String comment, String createBy, Info info) {
    this.announce = announce;
    this.announceList = announceList;
    this.creationDate = creationDate;
    this.comment = comment;
    this.createBy = createBy;
    this.info = info;
  }

  void setValue(String key, Object value) {
    if (!KEY_LIST.contains(key)) {
      throw DownloadException.asDownloadException("not contains this key " + key);
    } else {
      switch (key) {
        case "announce":
          this.setAnnounce(value.toString());
          break;
        case "announce-list":
          this.getAnnounceList().add(value.toString());
          break;
        case "creation date":
          if (StringUtils.isNumeric(value.toString())) {
            this.setCreationDate(Long.parseLong(value.toString()));
          } else {
            this.setCreationDate(0);
          }
          break;
        case "comment":
          this.setComment(value.toString());
          break;
        case "created by":
          this.setCreateBy(value.toString());
          break;
        case "length":
          List<Files> filesList1 = this.getInfo().getFiles();
          if (filesList1 != null) {
            Files files = this.getInfo().getFiles().get(filesList1.size() - 1);
            files.setLength(Long.parseLong(value.toString()));
          } else {
            this.getInfo().setLength(Long.parseLong(value.toString()));
          }
          break;
        case "md5sum":
          List<Files> filesList2 = this.getInfo().getFiles();
          if (filesList2 != null) {
            Files files = this.getInfo().getFiles().get(filesList2.size() - 1);
            files.setMd5sum(value.toString());
          } else {
            this.getInfo().setMd5sum(value.toString());
          }
          break;
        case "name":
          this.getInfo().setName(value.toString());
          break;
        case "piece length":
          this.getInfo().setPiecesLength(Long.parseLong(value.toString()));
          break;
        case "pieces":
          if (StringUtils.isNumeric(value.toString())) {
            this.getInfo().setPieces(null);
          } else {
            this.getInfo().setPieces((byte[]) value);
          }
          break;
        case "path":
          List<Files> filesList3 = this.getInfo().getFiles();
          Files files3 = filesList3.get(filesList3.size() - 1);
          files3.getPath().add(value.toString());
          break;
        default:
      }
    }
  }
}

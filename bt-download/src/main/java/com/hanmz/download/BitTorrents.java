package com.hanmz.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.out;

/**
 * *
 * Created by hanmz on 2017/10/9.
 */
public class BitTorrents {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  public static BitTorrentInfo parse(File btFile) throws Exception {
    return new BitTorrents().analyze(new FileInputStream(btFile));
  }

  private static BitTorrentInfo parse(String btFilePath) throws Exception {
    return new BitTorrents().analyze(new FileInputStream(btFilePath));
  }

  private BitTorrentInfo analyze(InputStream in) throws Exception {
    BitTorrentInfo btInfo = new BitTorrentInfo();
    String key = null;
    StringBuilder strLengthBuilder = new StringBuilder();
    int tempByte;
    while ((tempByte = in.read()) != -1) {
      char temp = (char) tempByte;
      switch (temp) {
        case 'i':
          StringBuilder itempBuilder = new StringBuilder();
          char iTemp;
          while ((iTemp = (char) in.read()) != 'e') {
            itempBuilder.append(iTemp);
          }
          btInfo.setValue(key, itempBuilder.toString());
          break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          strLengthBuilder.append(temp);
          break;
        case ':':
          int strLen = Integer.parseInt(strLengthBuilder.toString());
          strLengthBuilder = new StringBuilder();
          byte[] tempBytes = new byte[strLen];
          in.read(tempBytes);
          if (key != null && key.equals("pieces")) {
            btInfo.setValue(key, tempBytes);
          } else {
            String tempStr = new String(tempBytes);
            if (BitTorrentInfo.KEY_LIST.contains(tempStr)) {
              key = tempStr;
              switch (tempStr) {
                case "announce-list":
                  btInfo.setAnnounceList(new LinkedList<>());
                  break;
                case "info":
                  btInfo.setInfo(new Info());
                  break;
                case "files":
                  btInfo.getInfo().setFiles(new LinkedList<>());
                  btInfo.getInfo().getFiles().add(new Files());
                  break;
                case "length": {
                  List<Files> tempFiles = btInfo.getInfo().getFiles();
                  if (tempFiles != null && (tempFiles.isEmpty() || tempFiles.get(tempFiles.size() - 1).getLength() != 0)) {
                    tempFiles.add(new Files());
                  }
                  break;
                }
                case "md5sum": {
                  List<Files> tempFiles = btInfo.getInfo().getFiles();
                  if (tempFiles != null && (tempFiles.isEmpty() || tempFiles.get(tempFiles.size() - 1).getMd5sum() != null)) {
                    tempFiles.add(new Files());
                  }
                  break;
                }
                case "path":
                  List<Files> tempFilesList = btInfo.getInfo().getFiles();
                  if (tempFilesList.isEmpty()) {
                    Files files = new Files();
                    files.setPath(new LinkedList<>());
                    tempFilesList.add(files);
                  } else {
                    Files files = tempFilesList.get(tempFilesList.size() - 1);
                    if (files.getPath() == null) {
                      files.setPath(new LinkedList<>());
                    }
                  }
                  break;
                default:
              }
            } else {
              btInfo.setValue(key, tempStr);
            }
          }
          break;
        default:

      }
    }
    return btInfo;
  }

  public static void main(String[] args) throws Exception {
    BitTorrentInfo info = parse("C:\\Users\\Administrator\\Downloads\\杀破狼2.torrent");
    out.println("信息:" + info.getAnnounce() + "\t" + info.getComment() + "\t" + info.getCreateBy() + "\t" + new Date(info.getCreationDate()));
    Info it = info.getInfo();
    out.println("信息:" + it.getName() + "\t" + it.getPiecesLength() + "\t" + it.getLength() + "\t" + it.getMd5sum() + "\t" + Arrays.toString(it.getPieces()));

    if (!info.getAnnounceList().isEmpty()) {
      for (String str : info.getAnnounceList()) {
        out.println("信息2:" + str);
      }
    }

    if (!it.getFiles().isEmpty()) {
      for (Files file : it.getFiles()) {
        out.println("信息3:" + file.getLength() + "\t" + file.getMd5sum());
        if (!file.getPath().isEmpty()) {
          for (String str : file.getPath()) {
            out.println("信息4：" + str);
          }
        }
      }
    }
  }
}

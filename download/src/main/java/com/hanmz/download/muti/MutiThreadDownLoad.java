package com.hanmz.download.muti;

import com.hanmz.download.exception.DownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static com.hanmz.download.util.InnerUtils.fileSize;
import static com.hanmz.download.util.UrlEncodeUtils.getFileName;

/**
 * *
 * Created by hanmz on 2017/6/18.
 */
@Slf4j
public class MutiThreadDownLoad {
  AtomicLong cur;
  /**
   * 文件长度
   */
  int length;
  /**
   * 文件名
   */
  String filename;
  /**
   * 同时下载的线程数
   */
  private int threadCount;
  /**
   * 服务器请求路径
   */
  private String serverPath;
  /**
   * 本地路径
   */
  private String localPath;
  /**
   * 线程计数同步辅助
   */
  private CountDownLatch latch;
  private ExecutorService executor;

  MutiThreadDownLoad(int threadCount, String serverPath, String localPath, CountDownLatch latch) {
    this.threadCount = threadCount;
    this.serverPath = serverPath;
    this.localPath = localPath;
    this.latch = latch;
    this.executor = Executors.newFixedThreadPool(threadCount);
    this.cur = new AtomicLong();
  }

  void executeDownLoad() {
    try {
      URL url = new URL(serverPath);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(5000);
      conn.setRequestMethod("GET");
      int code = conn.getResponseCode();
      if (code == 200) {
        //服务器返回的数据的长度，实际上就是文件的长度,单位是字节
        length = conn.getContentLength();
        filename = getFileName(url, conn.getHeaderField("Content-Disposition"));
        log.info("文件总长度: {} 字节(Byte)", length);
        localPath = localPath + filename;

        try (RandomAccessFile raf = new RandomAccessFile(localPath, "rwd")) {
          //指定创建的文件的长度
          raf.setLength(length);
          raf.close();
          //分割文件
          int blockSize = length / threadCount;
          for (int threadId = 1; threadId <= threadCount; threadId++) {
            //第一个线程下载的开始位置
            int startIndex = (threadId - 1) * blockSize;
            int endIndex = startIndex + blockSize - 1;
            if (threadId == threadCount) {
              //最后一个线程下载的长度稍微长一点
              endIndex = length;
            }
            log.info("线程 {}, 下载: {}字节~{}字节", threadId, startIndex, endIndex);
            executor.submit(new DownLoadThread(threadId, startIndex, endIndex));
          }
        } catch (Exception e) {
          log.error("error", e);
          throw DownloadException.asDownloadException(e);
        }
      }

    } catch (Exception e) {
      log.error("error", e);
      throw DownloadException.asDownloadException(e);
    }
  }

  private class DownLoadThread implements Runnable {
    /**
     * 线程ID
     */
    private int threadId;
    /**
     * 下载起始位置
     */
    private int startIndex;
    /**
     * 下载结束位置
     */
    private int endIndex;

    DownLoadThread(int threadId, int startIndex, int endIndex) {
      this.threadId = threadId;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }


    @Override
    public void run() {
      try {
        log.info("线程 {},文件大小 {}, 正在下载...", threadId, fileSize(startIndex, endIndex));

        URL url = new URL(serverPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        //请求服务器下载部分的文件的指定位置, *****重要*****
        conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
        conn.setConnectTimeout(5000);
        int code = conn.getResponseCode();

        log.info("线程 {}, 请求返回code={}", threadId, code);

        // 开始读资源
        try (InputStream is = conn.getInputStream(); RandomAccessFile raf = new RandomAccessFile(localPath, "rwd")) {
          //随机写文件的时候从哪个位置开始写
          raf.seek(startIndex);//定位文件

          long curLen = 0;
          int len;
          byte[] buffer = new byte[1024];
          while ((len = is.read(buffer)) != -1) {
            curLen += len;
            raf.write(buffer, 0, len);
            cur.addAndGet(len);
          }
          log.info("线程 {} 下载完毕。文件长度 {}", threadId, fileSize(curLen));
          //计数值减一
          latch.countDown();
        } catch (Exception e) {
          log.error("error", e);
          throw DownloadException.asDownloadException(e);
        }

      } catch (Exception e) {
        log.error("error", e);
        throw DownloadException.asDownloadException(e);
      }

    }
  }
}
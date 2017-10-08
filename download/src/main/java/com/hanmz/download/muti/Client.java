package com.hanmz.download.muti;

import com.google.common.util.concurrent.Uninterruptibles;
import com.hanmz.download.exception.DownloadException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * *
 * Created by hanmz on 2017/6/18.
 */
@Slf4j
public class Client implements Runnable {
  private String url = "https://d11.baidupcs.com/file/f26e2613530bfad2ac003e8b411f19ab?bkt=p3-000070b5db40e9e35ead840ba79c9eb60c53&xcode=8b3716c55d6423c25852c6a4ce1f5bd2aaa61dc08280134da7103330c9091c9b&fid=2533457961-250528-217037575824989&time=1507349333&sign=FDTAXGERLQBHSK-DCb740ccc5511e5e8fedcff06b081203-%2BHf5FfKckFF%2FNLk2qwXGCh3cwNw%3D&to=d11&size=223896353&sta_dx=223896353&sta_cs=29620&sta_ft=mp4&sta_ct=7&sta_mt=6&fm2=MH,Yangquan,Netizen-anywhere,,liaoning,any&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=000070b5db40e9e35ead840ba79c9eb60c53&sl=79364174&expires=8h&rt=sh&r=847841532&mlogid=6482537231580918305&vuk=2022340328&vbdid=1977671850&fin=Fate+Zero+25.mp4&fn=Fate+Zero+25.mp4&rtype=1&iv=0&dp-logid=6482537231580918305&dp-callid=0.1.1&hps=1&tsl=100&csl=100&csign=qooouGf5wL0MGhW%2Fws5NwpUKCxA%3D&so=0&ut=6&uter=4&serv=1&uc=3851633048&ic=1980711064&ti=1ed35c59282f49f51b59de0af6d03bdb732b9a8838ce5022&by=themis";
  private String localPath = "D:/tmp/";
  private int threadSize = 32;
  /**
   * 执行进度
   */
  private long progress;
  private boolean finish;
  private String filename;

  public long getProgress() {
    return progress;
  }

  public boolean isFinish() {
    return finish;
  }

  public Client(String url, String localPath, int threadSize) {
    this.url = url;
    this.localPath = localPath;
    this.threadSize = threadSize;
  }

  public void run() {
    CountDownLatch latch = new CountDownLatch(threadSize);
    MutiThreadDownLoad m = new MutiThreadDownLoad(threadSize, url, localPath, latch);
    long startTime = System.currentTimeMillis();
    try {
      m.executeDownLoad();
      filename = m.filename;
      while (m.cur.get() < m.length) {
        progress = m.cur.get() * 100 / m.length;
        log.info("已下载:{}%", progress);
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
      }
      finish = true;
      latch.await();
    } catch (Exception e) {
      log.error("error", e);
      throw DownloadException.asDownloadException(e);
    }

    long endTime = System.currentTimeMillis();
    log.info("文件：{},下载完成,共耗时: {}s", filename, (endTime - startTime) / 1000);

  }
}

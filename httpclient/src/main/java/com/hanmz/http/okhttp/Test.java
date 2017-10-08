package com.hanmz.http.okhttp;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

/**
 * *
 * Created by hanmz on 2017/10/5.
 */
public class Test {
  private MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
  private OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true)
                                                          .connectTimeout(60, TimeUnit.SECONDS)
                                                          .readTimeout(60, TimeUnit.SECONDS)
                                                          .build();

  public static void main(String[] args) throws Exception {
    Test t = new Test();
    Response response = t.get("http://ip.taobao.com/service/getIpInfo.php?ip=63.223.108.42");
    System.out.println(response.body().string());

  }

  private Response psot(String url, String json) throws Exception {
    RequestBody body = RequestBody.create(mediaType, json);
    Request request = new Request.Builder().header("Upgrade-Insecure-Requests", "1")
                                           .header("Pragma", "no-cache")
                                           .header("Proxy-Connection", "keep-alive")
                                           .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                                           .header("Accept-Encoding", "gzip, deflate")
                                           .header("Accept-Language", "zh-CN,zh;q=0.8")
                                           .header("Cache-Control", "no-cache")
                                           .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                                           .header("Host", "ip.taobao.com")
                                           .header("Cookie", "miid=2108967369803816324; cookie2=180762aeb713ee352d197de7b69702b6; _tb_token_=cc6e0909d939b; t=25a63fd4cfc7aced14fa7ef63068f156; v=0")
                                           .url(url)
                                           .post(body)
                                           .build();
    return client.newCall(request).execute();
  }

  private Response get(String url) throws Exception {
    Request request = new Request.Builder()
//      .header("Upgrade-Insecure-Requests", "1")
                                           //                                           .header("Pragma", "no-cache")
                                           //                                           .header("Proxy-Connection", "keep-alive")
                                           //                                           .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                                           //                                           .header("Accept-Encoding", "gzip, deflate")
                                           //                                           .header("Accept-Language", "zh-CN,zh;q=0.8")
                                           //                                           .header("Cache-Control", "no-cache")
                                           .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                                           //                                           .header("Host", "ip.taobao.com")
                                           //                                           .header("Cookie", "miid=2108967369803816324; cookie2=180762aeb713ee352d197de7b69702b6; _tb_token_=cc6e0909d939b; t=25a63fd4cfc7aced14fa7ef63068f156; v=0")
                                           .url(url).build();
    return client.newCall(request).execute();
  }
}

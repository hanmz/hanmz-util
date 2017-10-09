package com.hanmz.http.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hanmz.http.exception.HttpException;
import com.hanmz.http.interceptor.HInterceptor;
import com.hanmz.http.interceptor.HRealChain;
import com.hanmz.http.interceptor.RetryInterceptor;
import com.hanmz.http.util.InnerUtils;
import com.hanmz.http.util.Utils;
import lombok.extern.slf4j.Slf4j;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
@Slf4j
public class HttpClient {
  private Map<String, Socket> map = Maps.newConcurrentMap();
  private List<HInterceptor> interceptors = Lists.newArrayList();

  public void addInterceptor(HInterceptor interceptor) {
    interceptors.add(interceptor);
  }

  /**
   * 增加interceptor chain
   */
  public void call(HRequest request) {
    request.setClient(this);
    List<HInterceptor> interceptorList = Lists.newArrayList();
    interceptorList.add(new RetryInterceptor());
    interceptorList.addAll(interceptors);
    HInterceptor.HChain chain = new HRealChain(request, interceptorList);
    try {
      chain.proceed(request);
    } catch (IOException e) {
      throw HttpException.asHttpException(e);
    }
  }

  public void get(HRequest request) {
    try {
      request.getBuffer().init();
      initGet(request.getUrl(), request.getHeaders(), request.getBuffer());
    } catch (Exception e) {
      throw HttpException.asHttpException(e);
    }
  }

  private void initGet(String url, Map<String, String> headers, HttpBuffer buffer) {
    try {
      String host = Utils.getHost(url);
      String path = Utils.getUri(url, host);

      Proxy proxy = getProxy(url);
      InetSocketAddress address = getAddress(host);
      Socket socket = getSocket(proxy, address);

      OutputStream out = socket.getOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(out);
      StringBuilder sb = new StringBuilder();

      sb.append("GET ").append(path).append(" HTTP/1.1").append("\r\n");
      headers.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\r\n"));
      sb.append("Host: ").append(host).append("\r\n");
      sb.append("Accept: text/html;charset=utf-8").append("\r\n");
      sb.append("\r\n");
      osw.write(sb.toString());
      osw.flush();

      buffer.in = socket.getInputStream();
    } catch (Exception e) {
      throw HttpException.asHttpException(e);
    }
  }

  private Proxy getProxy(String url) {
    ProxySelector proxySelector = ProxySelector.getDefault();
    URI uri = URI.create(url);
    List<Proxy> proxies = proxySelector.select(uri);

    return InnerUtils.isNullOrEmpty(proxies) ? Proxy.NO_PROXY : proxies.get(0);
  }

  private InetSocketAddress getAddress(String hostname) throws UnknownHostException {
    List<InetAddress> addresses = Arrays.asList(InetAddress.getAllByName(hostname));

    List<InetSocketAddress> inetSocketAddresses = Lists.newArrayList();
    for (InetAddress inetAddress : addresses) {
      inetSocketAddresses.add(new InetSocketAddress(inetAddress, 80));
    }

    return InnerUtils.isNullOrEmpty(inetSocketAddresses) ? null : inetSocketAddresses.get(0);
  }

  private Socket getSocket(Proxy proxy, InetSocketAddress address) {
    return map.compute(address.getHostName(), (k, v) -> {
      if (v == null || isClose(v)) {
        Utils.closeQuietly(v);
        return initSocket(proxy, address);
      }
      return v;
    });
  }

  public void cleanSocket(HRequest request) {
    String host = Utils.getHost(request.getUrl());
    Utils.closeQuietly(map.remove(host));
  }

  private Socket initSocket(Proxy proxy, InetSocketAddress address) {
    Socket socket = null;
    try {
      socket = proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP ? SocketFactory.getDefault().createSocket() : new Socket(proxy);

      // 读超时
      socket.setSoTimeout(5000);
      // 连接超时
      socket.connect(address, 5000);

      return socket;
    } catch (Exception e) {
      Utils.closeQuietly(socket);
      throw HttpException.asHttpException("init socket error", e);
    }
  }

  private boolean isClose(Socket socket) {
    try {
      socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
      return false;
    } catch (Exception se) {
      return true;
    }
  }
}

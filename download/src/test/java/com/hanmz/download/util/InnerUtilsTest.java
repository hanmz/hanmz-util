package com.hanmz.download.util;

import junit.framework.TestCase;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
public class InnerUtilsTest extends TestCase {
  public void testFileSize() throws Exception {
    String res = InnerUtils.fileSize(0, 100000000);
    System.out.println(res);
  }

}
package com.hanmz.http;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
public class Test {
  public static void main(String[] args) {
    Integer[] buffer1 = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] buffer2 = new int[10];

    System.arraycopy(buffer1, 5, buffer1, 0, 5);

    System.out.println(printfArr(buffer1));

  }

  private static <T> String printfArr(T[] arr){
    StringBuilder sb= new StringBuilder();
    for (T t : arr) {
      sb.append(t).append(' ');
    }
    return sb.toString();
  }
}

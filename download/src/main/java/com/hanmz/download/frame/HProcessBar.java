package com.hanmz.download.frame;

import com.google.common.util.concurrent.Uninterruptibles;
import com.hanmz.download.muti.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
class HProcessBar extends JFrame {

  private JProgressBar processBar;

  public static void main(String[] args) {
    HProcessBar bar = new HProcessBar(new Client("", "", 1));
    bar.setVisible(true);
  }

  HProcessBar(Client client) {
    setTitle("进度");      //设置窗体标题
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // 设置窗体退出的操作
    double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    setBounds((int) (lx / 2) - 120, (int) (ly / 2) - 120, 250, 100);// 设置窗体的位置和大小
    JPanel contentPane = new JPanel();   // 创建内容面板
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));// 设置内容面板边框
    setContentPane(contentPane);// 应用(使用)内容面板
    contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));// 设置为流式布局
    processBar = new JProgressBar();// 创建进度条
    processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示
    processBar.setBackground(Color.GREEN);

    // 创建线程显示进度
    new Thread(() -> {
      while (!client.isFinish()) {
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        processBar.setValue((int) client.getProgress()); // 设置进度条数值
      }
      processBar.setString("下载完成");// 设置提示信息
    }).start(); //  启动进度条线程

    contentPane.add(processBar);// 向面板添加进度控件
  }

}

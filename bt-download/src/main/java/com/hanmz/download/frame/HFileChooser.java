package com.hanmz.download.frame;

import com.hanmz.download.muti.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *
 * Created by hanmz on 2017/10/7.
 */
@Slf4j
public class HFileChooser implements ActionListener {
  private static final ExecutorService downloadService = Executors.newCachedThreadPool();
  private JFrame frame = new JFrame("并行下载器");
  private JPanel jp = new JPanel();
  private Container con = new Container();//布局1
  private JLabel threadSizeLabel = new JLabel("并发数：");
  private JLabel urlLabel = new JLabel("url：");
  private JLabel pathUrl = new JLabel("存储位置：");
  private JTextField threadSizeText = new JTextField("32");
  private JTextField urlText = new JTextField();
  private JTextField pathText = new JTextField("D:\\tmp");
  /**
   * 选择文件button
   */
  private JButton fileButton = new JButton("...");
  /**
   * 文件选择器
   */
  private JFileChooser jfc = new JFileChooser();
  /**
   * download button
   */
  private JButton downloadButton = new JButton("下载");
  private JButton testButton = new JButton("test");

  HFileChooser() {
    //文件选择器的初始目录定为d盘
    jfc.setCurrentDirectory(new File("d:\\"));

    //下面两行是取得屏幕的高度和宽度
    double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));//设定窗口出现位置
    frame.setSize(500, 350);//设定窗口大小

    //下面设定标签等的出现位置和高宽
    threadSizeLabel.setBounds(10, 10, 100, 30);
    urlLabel.setBounds(10, 50, 100, 30);
    pathUrl.setBounds(10, 90, 100, 30);

    threadSizeText.setBounds(80, 10, 200, 30);
    urlText.setBounds(80, 50, 200, 30);
    pathText.setBounds(80, 90, 200, 30);

    fileButton.setBounds(300, 90, 50, 30);
    fileButton.addActionListener(this);//添加事件处理

    downloadButton.setBounds(10, 120, 80, 30);
    downloadButton.addActionListener(this);//添加事件处理

    testButton.setBounds(10, 220, 80, 30);
    testButton.addActionListener(this);//添加事件处理

    con.add(threadSizeLabel);
    con.add(urlLabel);
    con.add(pathUrl);
    con.add(threadSizeText);
    con.add(urlText);
    con.add(pathText);
    con.add(fileButton);
    con.add(jfc);
    con.add(downloadButton);

    frame.setContentPane(con);//设置布局
    frame.setVisible(true);//窗口可见
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//使能关闭窗口，结束程序
  }

  public void actionPerformed(ActionEvent e) {//事件处理
    if (e.getSource().equals(fileButton)) {//判断触发方法的按钮是哪个
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设定只能选择到文件夹
      int state = jfc.showOpenDialog(null);//此句是打开文件选择器界面的触发语句
      if (state != 1) {
        File f = jfc.getSelectedFile();//f为选择到的目录
        pathText.setText(f.getAbsolutePath());
      }
    }

    if (e.getSource().equals(downloadButton)) { // 下载文件
      String threadSize = threadSizeText.getText();
      String url = urlText.getText();
      String path = pathText.getText();
      try {
        Client client = new Client(url, path, NumberUtils.toInt(threadSize));
        downloadService.submit(client);
        HProcessBar bar = new HProcessBar(client);
        bar.setVisible(true);
      } catch (Exception ex) {
        log.error("error", e);
        JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }
}

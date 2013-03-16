/*
  * TrayDemo.java 
  * 创建于  2013-3-5
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.swing;

/**
 * @author Administrator
 *
 */
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/**
 * Java1.6.0实现系统托盘技术演示
 *
 * @author 五斗米 <如转载请保留作者和出处>
 * @blog http://blog.csdn.net/mq612
 */
public class TrayDemo extends JFrame {
    private JPanel pane = null;
    private JButton button = null; // 启动托盘图标的按钮
    private JLabel label = null; // 用来显示系统是否支持托盘的信息
    private TrayIcon trayIcon = null; // 托盘图标
    private SystemTray tray = null; // 本操作系统托盘的实例
   
    public TrayDemo() {
        super("Java1.6.0托盘技术演示");
        try {
            // 将LookAndFeel设置成Windows样式
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        pane = new JPanel();
        button = new JButton("缩小到托盘");
        button.setEnabled(false);
        label = new JLabel("本操作系统不支持托盘");
        pane.add(label);
        pane.add(button);
        if(SystemTray.isSupported()){ // 如果操作系统支持托盘
            this.tray();
        }
        this.getContentPane().add(pane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setVisible(true);
    }
    /**
     * 托盘相关代码
     */
    private void tray(){
        label.setText("本操作系统支持托盘");
        button.setEnabled(true);
        tray = SystemTray.getSystemTray(); // 获得本操作系统托盘的实例
        ImageIcon icon = new ImageIcon("images/icon.gif"); // 将要显示到托盘中的图标
        PopupMenu pop = new PopupMenu(); // 构造一个右键弹出式菜单
        MenuItem show = new MenuItem("显示窗口");
        MenuItem exit = new MenuItem("退出演示");
        MenuItem author = new MenuItem("Author");
        /**
         * TrayIcon有三个构造
         * TrayIcon(Image image) 用“图标”来构造
         * TrayIcon(Image image, String tooltip) 用“图标”和“ToolTip”构造
         * TrayIcon(Image image, String tooltip, PopupMenu popup) 用“图标”，“ToolTip”，“弹出菜单”来构造一个托盘图标
         */
        trayIcon = new TrayIcon(icon.getImage(), "Java1.6.0托盘技术演示", pop);
        // 点击本按钮后窗口被关闭，托盘图标被添加到系统的托盘中
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    tray.add(trayIcon); // 将托盘图标添加到系统的托盘实例中
                    setVisible(false); // 使窗口不可视
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });
        /**
         * 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口
         */
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){ // 鼠标双击
                    tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
                    setVisible(true); // 显示窗口
                }
            }
        });
        show.addActionListener(new ActionListener() { // 点击“显示窗口”菜单后将窗口显示出来
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
                setVisible(true); // 显示窗口
            }
        });
        exit.addActionListener(new ActionListener() { // 点击“退出演示”菜单后退出程序
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 退出程序
            }
        });
        author.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMessage();
            }
        });
        pop.add(show);
        pop.add(exit);
        pop.add(author);
    }
    /**
     * 显示信息
     */
    private void showMessage(){
        JOptionPane.showMessageDialog(this, 
                new JLabel("<html>作者：mq612(五斗米)<br>Blog：http://blog.csdn.net/mq612</html>"), 
                       "五斗米", JOptionPane.INFORMATION_MESSAGE);
    }
   
    public static void main(String[] args) {
        new TrayDemo();
    }
   
}
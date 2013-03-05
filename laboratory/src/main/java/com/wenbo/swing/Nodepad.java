/*
  * Nodepad.java 
  * 创建于  2013-3-5
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.swing;

/**
 * @author Administrator
 *
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
public class Nodepad extends JFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea editor;
    private Container c;
    private Font f=new Font("sanserif",Font.PLAIN,12);
    //菜单栏成员属性声明
    private JMenuBar mb;        //菜单棒
    private JMenu fileMenu;     //文件菜单
    private JMenu editMenu;     //编辑菜单
    private JMenu formatMenu;   //格式菜单
    private JMenuItem fileMenuOpen,fileMenuSave,fileMenuExit;   //文件菜单的菜单项
    private JMenuItem editMenuCopy,editMenuCut,editMenuPaste;   //编辑菜单的菜单项
    private JMenu formatSet;//格式菜单第一级菜单
    private JMenuItem lineWrap,cancleLineWrap,wrapStyleWord,cancleWrapStyleWord;//格式菜单的第二级菜单
 
    //工具栏成员属性声明
    private JToolBar toolBar;   //工具棒
    private JButton b1,b2,b3,b4,b5; //声明5个按钮，分别为：“打开”、“保存”、“复制”、“剪切”、“粘贴”
     
    //弹出式菜单属性声明
    private JPopupMenu pm;
    private JMenuItem item1,item2,item3,item4,item5;
 
 
 
    public Nodepad()
    {
        super("简单记事本");
        setSize(400,300);
     
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){ System.err.println("不能被设置外观的原因:"+e);}
         
 
 
        c=getContentPane();         //创建一个内容面板
        editor = new JTextArea();   //创建一个文本区
        c.add(new JScrollPane(editor)); //设置滚动条，并添加到内容面板
 
        //菜单栏的实现
        //文件菜单的实现
        mb = new JMenuBar();    //创建菜单棒
        fileMenu = new JMenu("文件(F)");//创建菜单
        fileMenuOpen = new JMenuItem("打开(O)...Ctrl+O");
        fileMenuSave = new JMenuItem("保存(S)...Ctrl+S");
        fileMenuExit = new JMenuItem("退出");
        JMHandler JM=new JMHandler();       //创建监听器
        fileMenuOpen.addActionListener(JM); //注册监听器
        fileMenuSave.addActionListener(JM);
        fileMenuExit.addActionListener(JM);
        fileMenu.add(fileMenuOpen);
        fileMenu.add(fileMenuSave);
        fileMenu.addSeparator();    //添加分隔线
        fileMenu.add(fileMenuExit);
        fileMenu.setFont(f);        //设置菜单中文体的字体
 
         
        //编辑菜单的实现
        editMenu = new JMenu("编辑(E)");
        editMenuCopy = new JMenuItem("复制(C) Ctrl+C");
        editMenuCut = new JMenuItem("剪切(T) Ctrl+X");
        editMenuPaste = new JMenuItem("粘贴(P) Ctrl+v");
        EMHandler EM=new EMHandler();   //创建监听器
        editMenuCopy.addActionListener(EM);//注册监听器
        editMenuCut.addActionListener(EM);
        editMenuPaste.addActionListener(EM);
        editMenu.add(editMenuCopy);
        editMenu.add(editMenuCut);
        editMenu.add(editMenuPaste);
        editMenu.setFont(f);
 
 
        //格式菜单的实现
        formatMenu = new JMenu("格式(O)");            //创建菜单
        formatSet = new JMenu("格式设置");          //二级菜单
        lineWrap = new JMenuItem("自动换行");       //二级菜单项
        cancleLineWrap = new JMenuItem("取消自动换行");
        wrapStyleWord = new JMenuItem("断行不断字");
        cancleWrapStyleWord = new JMenuItem("取消断行不断字");
        FMHandler FM=new FMHandler();               //创建事件监听器
        lineWrap.addActionListener(FM);             //注册二级菜单项的监听器
        cancleLineWrap.addActionListener(FM);
        wrapStyleWord.addActionListener(FM);
        cancleWrapStyleWord.addActionListener(FM);
        formatSet.add(lineWrap);
        formatSet.add(cancleLineWrap);
        formatSet.addSeparator();   //添加分隔线
        formatSet.add(wrapStyleWord);
        formatSet.add(cancleWrapStyleWord);
        formatMenu.add(formatSet);
        formatMenu.setFont(f);
 
 
        //将菜单全部添加菜单棒里
        mb.add(fileMenu);
        mb.add(editMenu);
        mb.add(formatMenu);
 
        //工具栏的实现
        //按钮分别为：“打开”、“保存”、“复制”、“剪切”、“粘贴”
        toolBar =new JToolBar();    //创建工具棒
        b1= new JButton(new ImageIcon("img/open.gif"));
        b2= new JButton(new ImageIcon("img/save.gif"));
        b3= new JButton(new ImageIcon("img/copy.gif"));
        b4= new JButton(new ImageIcon("img/cut.gif"));
        b5= new JButton(new ImageIcon("img/paste.gif"));
        TBHandler TB=new TBHandler();       //创建按钮监听器
        b1.addActionListener(TB);   b2.addActionListener(TB);
        b3.addActionListener(TB);   b4.addActionListener(TB);
        b5.addActionListener(TB);
        //把按钮全部添加到工具棒中
        toolBar.add(b1);    toolBar.add(b2);
        toolBar.add(b3);    toolBar.add(b4);
        toolBar.add(b5);
 
 
        //弹出菜单的实现
        pm =new JPopupMenu();   //创建弹出菜单
        item1 = new JMenuItem("打开");
        item2 = new JMenuItem("保存");
        item3 = new JMenuItem("复制");
        item4 = new JMenuItem("剪切");
        item5 = new JMenuItem("粘贴");
        JPHandler JP=new JPHandler();
        item1.addActionListener(JP);  //注册菜单项的鼠标事件监听器
        item2.addActionListener(JP);
        item3.addActionListener(JP);
        item4.addActionListener(JP);
        item5.addActionListener(JP);
        editor.addMouseListener(JP);  //注册文本区的鼠标事件监听器
        pm.add(item1);  pm.add(item2);
        pm.add(item3);  pm.add(item4);
        pm.add(item5);
 
 
 
 
 
         
        //把菜单栏、工具栏、弹出菜单添加到内容面板
        setJMenuBar(mb);        //显示菜单栏
        c.add(toolBar,BorderLayout.NORTH);
         
 
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
 
 
 
    /*自定义类实现文件菜单项的事件处理*/
    private class JMHandler implements ActionListener
    {  
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==fileMenuOpen){ loadFile(); }
            else if(e.getSource()==fileMenuSave){ saveFile(); }
            else{System.exit(0);}
        }
 
    }
 
    public void loadFile()  //打开文件方法
    {
        JFileChooser fc=new JFileChooser();
        int r=fc.showOpenDialog(this);
        if(r==JFileChooser.APPROVE_OPTION)
        {
            File file=fc.getSelectedFile();
            try{ editor.read(new FileReader(file),null);}
            catch(IOException e){}
        }
    }
         
    public void saveFile()  /*保存文件的方法*/
    {
        JFileChooser fc=new JFileChooser();
        int r=fc.showSaveDialog(this);
        if(r==JFileChooser.APPROVE_OPTION)
        {
            File file=fc.getSelectedFile();
            try{ editor.write(new FileWriter(file));}
            catch(IOException e){}
        }
    }
 
     
    //编辑菜单菜单项的事件处理
    private class EMHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==editMenuCopy)     //实现复制功能
            {
                editor.copy();
                editor.requestFocus();
            }
            else if(e.getSource()==editMenuCut)//实现剪切功能
            {
                editor.cut();
                editor.requestFocus();
            }
            else                                //实现粘贴功能
            {
                editor.paste();
                editor.requestFocus();
            }
        }
    }
    //格式菜单二级菜单的菜单项的事件处理
    private class FMHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==lineWrap){ editor.setLineWrap(true); }
            else if(e.getSource()==cancleLineWrap) { editor.setLineWrap(false);}
            else if(e.getSource()==wrapStyleWord) { editor.setWrapStyleWord(true);}
            else{ editor.setWrapStyleWord(false); }
        }
    }
 
 
    //自定义类实现工具栏的按钮事件处理
    private class TBHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==b1){ loadFile(); }        //实现打开文件功能
            else if(e.getSource()==b2) {saveFile();}    //实现保存文件功能
            else if(e.getSource()==b3)  //文件复制
            {
                editor.copy();
                editor.requestFocus();
            }
            else if(e.getSource()==b4) //文件剪切
            {
                editor.cut();
                editor.requestFocus();
            }
            else                        //文件粘贴
            {
                editor.paste();
                editor.requestFocus();
            }
        }
    }
 
    //自定义类实现弹出式菜单的事件处理
    private class JPHandler implements ActionListener,MouseListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==item1){ loadFile(); }     //实现打开文件功能
            else if(e.getSource()==item2) {saveFile();} //实现保存文件功能
            else if(e.getSource()==item3)   //文件复制
            {
                editor.copy();
                editor.requestFocus();
            }
            else if(e.getSource()==item4) //文件剪切
            {
                editor.cut();
                editor.requestFocus();
            }
            else                        //文件粘贴
            {
                editor.paste();
                editor.requestFocus();
            }
        }
 
        public void mouseReleased(MouseEvent e)
        {
            if(e.isPopupTrigger())                  //判断是否按下鼠标右键
                pm.show(editor,e.getX(),e.getY());  //显示弹出式菜单
        }
 
        public void mouseClicked(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mousePressed(MouseEvent e){}
    }
 
     
 
    public static void main(String []args)
    {
        Nodepad N=new Nodepad();
    }
         
}
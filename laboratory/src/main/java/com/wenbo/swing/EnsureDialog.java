/*
  * EnsureDialog.java 
  * 创建于  2013-3-5
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.swing;

/**
 * @author Administrator
 *
 */
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/***************************
 * 设置弹出消息框，应用于确认
 * ************************/
public class EnsureDialog extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int n;
	public EnsureDialog(String s){		
		n = JOptionPane.showConfirmDialog(this,s,"确认对话框", JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION){
			
		}
		else if(n==JOptionPane.NO_OPTION){
			
		}
		JOptionPane.showMessageDialog(this,"有风险", "Warning",JOptionPane.WARNING_MESSAGE);
	}
	public int getN(){
		return n;
	}
	
	public static void main(String[] args){
		EnsureDialog ensureDialog = new EnsureDialog("aaaa");
		ensureDialog.dispose();
	}
}

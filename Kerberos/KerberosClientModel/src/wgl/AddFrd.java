package wgl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import Client.ConnManger;
import Client.SocketConn;
import Message.Message;

public class AddFrd extends JFrame implements ActionListener{

	/**
	 * @author yuxue
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel back;
	private JLabel text=new JLabel();
	private JTextField usrName = new JTextField("用户id");		//创建带有初始化文本的文本框对象
	private JButton AddBtn=new JButton();
	private JButton Exit=new JButton();
	
	Border b = new LineBorder(Color.GRAY, 1); 	//按钮边界线的设置
	
	public AddFrd(){
		
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle("添加好友");
		this.setSize(450,300);
		
		//设置运行位置，是对话框居中
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width-350)/2,(int)(screenSize.height-600)/2+45);
		
		//设置面板背景
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("添加.jpg"));
		back.setIcon(icon);
		back.setBounds(0, -15, 450, 350);
		
		//提示用户输入信息
		text.setBounds(205, 60, 160, 50);
		text.setFont(new Font("Serif",Font.PLAIN,18));
		text.setText("要添加的用户ID:");
		
		//用于输入用户名
		usrName.setForeground(Color.gray);
		usrName.setBounds(185, 120, 180, 30);
		usrName.setFont(new Font("Serif",Font.PLAIN,12));
		
		//添加“添加”按钮
		AddBtn.setText("添加");
		AddBtn.setFont(new Font("Dialog",0,12));
		AddBtn.setBounds(189, 180, 60, 30);
		AddBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		AddBtn.setBackground(Color.white);
		AddBtn.setBorder(b);
		AddBtn.setVisible(true);

		//添加“关闭”按钮
		Exit.setText("关闭");
		Exit.setFont(new Font("Dialog",0,12));		
		Exit.setBounds(300, 180, 60, 30);
		Exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Exit.setBackground(Color.WHITE);
		Exit.setBorder(b);
		Exit.setVisible(true);
		
		//添加按钮监听事件
		AddBtn.addActionListener(this);
		Exit.addActionListener(this);
			
		this.getContentPane().add(usrName);
		this.getContentPane().add(text);	
		this.getContentPane().add(AddBtn);
		this.getContentPane().add(Exit);
		this.getContentPane().add(back);
		this.setVisible(false);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new AddFrd();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==AddBtn){//点击的按钮是添加
			
			String Content = usrName.getText();	//获取文本框内容				
			
			if(Content.equals("")){
				JOptionPane.showMessageDialog(null, "请输入完整信息!");
			}
			else{
				//服务器交互逻辑
				
			}
		}
		else if(e.getSource()==Exit){//点击的按钮是b2
			this.setVisible(false);
		}
	}
}


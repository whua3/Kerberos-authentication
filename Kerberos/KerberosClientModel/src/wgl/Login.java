package wgl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import APP.Application;
import Client.ConnManger;
import Client.SocketConn;
import Client.PrepareConn;
import Databean.User;
import Message.*;
import Security.DES.Des;

public class Login extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel back;
	private JLabel jt1=new JLabel();
	private JLabel jt2=new JLabel();
	private JTextField jt = new JTextField("输入用户名");		//创建带有初始化文本的文本框对象
	private JPasswordField jp=new JPasswordField(20);
	private JButton xa=new JButton();
	private JButton xb=new JButton();
	
	public Login(){
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle("登陆");
		this.setSize(450,350);
		
		//设置运行位置，是对话框居中
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width-350)/2,(int)(screenSize.height-600)/2+45);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("登陆.jpg"));
		back.setIcon(icon);
		back.setBounds(-0, 0, 450, 350);
		
		jt.setForeground(Color.gray);
		jt.setBounds(95, 100, 150, 30);
		jt.setFont(new Font("Serif",Font.PLAIN,12));
		
		
		jt1.setBounds(40, 90, 80, 50);
		jt1.setFont(new Font("黑体",Font.PLAIN,16));
		jt1.setForeground(Color.BLACK);
		jt1.setText("用户名:");
		
		//创建密码框
		jp.setFont(new Font("Serif",Font.PLAIN,12));
		jp.setBounds(95, 150, 150, 30);
		jp.setVisible(true);
		
		jt2.setBounds(40, 140, 80, 50);
		jt2.setFont(new Font("黑体",Font.PLAIN,16));
		jt2.setForeground(Color.BLACK);
		jt2.setText("口  令： ");
		
		xa.setText("登陆");
		xa.setFont(new Font("Dialog",0,12));
		xa.setBounds(95, 200, 150, 30);
		xa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		xa.setBackground(getBackground());
		xa.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		xa.setBorder(b);
		xa.setVisible(true);

		
		xb.setText("注册");
		xb.setFont(new Font("Dialog",0,12));
		xb.setBounds(185, 200, 60, 30);
		xb.setBackground(Color.WHITE);
		xb.setVisible(false);
		xb.setBorder(b);
		xb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		xa.addActionListener(this);
		xb.addActionListener(this);
		
	
		this.getContentPane().add(jt);
		this.getContentPane().add(jt1);
		this.getContentPane().add(jt2);
		this.getContentPane().add(jp);	
		this.getContentPane().add(xa);
		this.getContentPane().add(xb);
		
		this.getContentPane().add(back);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Login();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==xa){//点击的按钮是登录	
			
			String usr=jt.getText().toString();	//获取文本框内容			
			char[] passwords = jp.getPassword();			
			String password =String.valueOf(passwords);	//获取密码框内容
			
			String Content=usr+password;
			
			if(usr.equals("")||password.equals("")){
				//System.out.println("请输入完整信息!");
				JOptionPane.showMessageDialog(null, "请输入完整信息!");
			}
			else{
				
				xb.setVisible(false);
				xa.setText("正在登陆...");
				xa.setBounds(95, 200, 150, 30);
				this.setVisible(true);
				
				long usrId = Long.parseLong(jt.getText());
				boolean goon = false;
				try {
					if(PrepareConn.returnKerberos(usrId, password)){
						goon = true;
					}else{
						JOptionPane.showMessageDialog(null, "认证错误!");
					}
					
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if(goon){
				Application.cm = new ConnManger("chatserver");
				SocketConn conn = Application.cm.getConn();
				if(conn == null){
					System.out.println("null");
				}
				byte[] buffer = new byte[8216];
				byte[] message = Message.getRespondMessage(Application.PSERVERCHAT, usrId, (byte)1, Application.ON_LINE, null);
				try {
					System.out.println(new String(message));
					message = Des.encrypt(message); //Des加密
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				conn.send(message);
				conn.receive(buffer);
				try {
					buffer = Des.decrypt(buffer); //Des解密
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(Message.getRespond(buffer) == Application.SUCCESS){
					Application.user = new User(usrId, new String(Message.getContent(buffer)));
					
					message = Message.getRespondMessage(Application.PSERVERCHAT, usrId, (byte)1, Application.GET_FRIEND, null);
					try {
						
						message = Des.encrypt(message); //Des加密
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					conn.send(message);
					conn.receive(buffer);
					try {
						buffer = Des.decrypt(buffer); //Des解密
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.setVisible(false);
					List<User> user = ContentTool.getUsser(Message.getContent(buffer));
					Chat main =  Chat.getInstance(Application.user.getName(),user);
				}
				else{//出错提示
					JOptionPane.showMessageDialog(null, "服务器返回一个错误!");
				}
				
				}
			}
		}
		else if(e.getSource()==xb){//点击的按钮是b2
			new Regist();
			setVisible(false);
		}
	}
}

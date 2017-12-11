package wgl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import APP.Application;
import Databean.User;
import Security.DES.Des;

public class Chat extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JLabel top;
	private JLabel body;
	private JButton add_bt;
	private List<User> user = null;
	private JScrollPane scroll;
	
	public static Map<Long, GpChat> usrSet = new HashMap<Long, GpChat>();
	private AddFrd addfrd = null;
	
	private static Chat instance = null;
	
	
	private Chat(String name, List<User> user){
		
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle(name);
		this.setSize(280,600);
		this.user = user;
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width-350)/2, (int)(screenSize.height-600)/2);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	//关闭应用， 服务器交互逻辑
	        	byte[] message = Message.Message.getRespondMessage(Application.PSERVERCHAT, Application.user.getId(), (byte)1, Application.OFF_LINE, null);
	        	try {
					message = Des.encrypt(message);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Application.cm.getConn().send(message);
				try {
					Application.cm.getConn().close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	System.exit(0);
	        }
		});
		
		top=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("主题.jpg"));
		top.setIcon(icon);
		top.setBounds(0,-150, 450, 400);
		
		
		body=new JLabel();
		ImageIcon ic=new ImageIcon(this.getClass().getResource("面板.jpg"));
		body.setIcon(ic);
		//body.setBounds(0,100, 450, 700);
		
		addfrd = new AddFrd();
		add_bt = new JButton("添加好友");
		add_bt.setBounds(180, 535, 90, 30);
		add_bt.setFont(new Font("Dialog",0,12));
		add_bt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add_bt.setBackground(getBackground());
		add_bt.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		add_bt.setBorder(b);
		add_bt.setVisible(false);
		add_bt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addfrd.setVisible(true);
			}
		
		});
		
		initBody();
		
		this.getContentPane().add(add_bt);
		this.getContentPane().add(scroll);
		this.getContentPane().add(top);
		
		this.setVisible(true);
		
		new BackgroundThread().start();
	}
	
	public static Chat getInstance(String name, List<User> user){
		if(instance == null)
			instance = new Chat(name, user);
		return instance;
	}
	
	
	public void initBody(){
		
		int i = 0;
		for(final  User u : user){
			JPanel user_jp = new JPanel();
			user_jp.setLayout(null);
			user_jp.setBounds(10, 5+65*(i++), 260, 60);
			user_jp.setOpaque(false);
			
			JLabel name = new JLabel(u.getName());
			name.setBounds(70, 0, 100, 50);
			name.setFont(new Font("Dialog",Font.BOLD,18));
			name.setVisible(true);
			
			ImageIcon img = new ImageIcon(this.getClass().getResource("头像.jpg"));
			
			JButton head_img = new JButton(img);
			
			usrSet.put(u.getId(), new GpChat(u));
			head_img.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					usrSet.get(u.getId()).setVisible(true);
				}
			});
			
			head_img.setBounds(4, 10, 50, 45);
			head_img.setVisible(true);
			head_img.setBorderPainted(false);		//设置边框
			
			user_jp.add(name);
			user_jp.add(head_img);
			body.add(user_jp);
		}
		scroll = new JScrollPane(body);
		scroll.setBounds(0,100, 450, 700);
	}
	
	public static void main(String[] argv){
		List<User> user = new ArrayList<User>();
		user.add(new User(1l,"gao"));
		getInstance("gao", user);
	}
}

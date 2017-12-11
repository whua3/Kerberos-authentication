package wgl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class Regist extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JLabel back;
	private JLabel usr=new JLabel();
	private JLabel pwd=new JLabel();
	private JLabel jl=new JLabel();
	private JTextField jt = new JTextField("输入用户名");		//创建带有初始化文本的文本框对象
	private JPasswordField jp1=new JPasswordField(20);
	private JPasswordField jp2=new JPasswordField(20);
	private JButton x=new JButton();
	
	public Regist(){
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle("注册");
		this.setSize(450,350);
		
		//设置运行位置，是对话框居中
				Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
				this.setLocation((int)(screenSize.width-350)/2,
						(int)(screenSize.height-600)/2+45);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("regist.jpg"));
		back.setIcon(icon);
		back.setBounds(0, -20, 450, 350);		
		
		usr.setBounds(175, 40, 80, 50);
		usr.setFont(new Font("黑体",Font.PLAIN,14));
		usr.setForeground(Color.BLACK);
		usr.setText("用户名:");		
		jt.setForeground(Color.gray);
		jt.setBounds(230, 50, 150, 30);
		jt.setFont(new Font("Serif",Font.PLAIN,12));
		
		pwd.setBounds(175, 85, 80, 50);
		pwd.setFont(new Font("黑体",Font.PLAIN,14));
		pwd.setForeground(Color.BLACK);
		pwd.setText("口  令： ");		
		//创建密码框
		jp1.setFont(new Font("Serif",Font.PLAIN,12));
		jp1.setBounds(230, 95, 150, 30);
		jp1.setVisible(true);
		
		jl.setBounds(165, 130, 80, 60);
		jl.setFont(new Font("黑体",Font.PLAIN,14));
		jl.setForeground(Color.BLACK);
		jl.setText("口令确认： ");		
		jp2.setFont(new Font("Serif",Font.PLAIN,12));
		jp2.setBounds(230, 140, 150, 30);
		jp2.setVisible(true);

		
		x.setText("立即注册");
		x.setFont(new Font("Dialog",0,12));
		x.setBounds(260,200, 90, 30);
		x.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		x.setBackground(getBackground());
		x.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		x.setBorder(b);
		x.setVisible(true);
		
		x.addActionListener(this);
		this.getContentPane().add(jt);
		this.getContentPane().add(usr);
		this.getContentPane().add(pwd);
		this.getContentPane().add(jl);
		this.getContentPane().add(jp1);	
		this.getContentPane().add(jp2);	
		this.getContentPane().add(x);

		this.getContentPane().add(back);
		this.setVisible(true);
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {					
		String usr=jt.getText().toString();	//获取文本框内容					
		String password1 =String.valueOf(jp1.getPassword());	//获取密码框内容			
		String password2 =String.valueOf(jp2.getPassword());	//获取密码框内容					
		String Content=usr+password1+password2;
					
		if(usr.equals("")||password1.equals("")||password2.equals("")){
			//System.out.println("请输入完整信息!");
			JOptionPane.showMessageDialog(null, "请输入完整信息!");
			jp1.setText(null);
	        jp2.setText(null);
			}
		else if(password1.equals(password2)){
			//此处是服务器交互逻辑
				
			
			JOptionPane.showMessageDialog(null, "注册成功");
			setVisible(false);			
			}	
		else{
			 JOptionPane.showMessageDialog(null, "两次输入的密码不一致，请重新输入！");
	         jp1.setText(null);
	         jp2.setText(null);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Regist();
	}
}

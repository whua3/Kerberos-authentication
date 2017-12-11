package Servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;
import DBManger.DBExcute;
import Message.Message;
import Security.DES.Des;
import Security.MD5.MD5;
import Security.RSA.RSA;

//这是服务器的一个线程
public class AS implements Runnable{
	 private Socket socket;
	 byte [] message=new byte[8216];
	 SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	 private final long MAXTIME=100000; 
	 
	 //源
	 private final int AS = 1;//AS服务器
	 private final int TGS = 2;//TGS服务器
	 private final int PSERVERCHAT = 3;//应用服务器（消息）
//	 private final int PSERVERFILE = 4;//应用服务器（文件）
	 
	 //反馈respond
	 private final byte NEGATIVE = 0;//该字段未被激活
	 private final byte SUCCESS = 1;//操作成功
	 private final byte DENY = 2;//拒绝服务
	 private final byte FAILTOUPLOAD = 3;//文件上传失败
	 private final byte FAILTODOWNLOAD = 4;//文件下载失败
	 private final byte USEROFFLINE = 5;//消息投递成功，但用户处于离线状态
	 private final byte OVERTIME = 6;//消息超时
	 
	 private Logger log = Logger.getLogger("Connect-Status-Log"); //日志
	 
	 final static int MAX_SIZE = 8216;
	 
	 public AS(Socket socket){
		 this.socket=socket;
	 }
	 
	 public void send(byte[] content){
			try {
				OutputStream socketOut = socket.getOutputStream();
				socketOut.write(content);
				socketOut.flush();
				log.info("Messeag has been sent!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.warning("Fail to send Messeag due to IOException!");
				e.printStackTrace();
			}
		}
	 
	 public int receive(byte[] result){
			int len = -1;
			try {
				InputStream socketIn = socket.getInputStream();
				len = socketIn.read(result, 0, MAX_SIZE);
				log.info("Messeag has been received!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.warning("Fail to receive Messeag due to IOException!");
				e.printStackTrace();
			}
			return len;
	}
	 
	 
	@Override
	/**
	 * 服务器逻辑
	 */
	public void run() {		
		System.out.println("New connection accepted "+socket.getInetAddress()+":"+socket.getPort());
		try {
			byte[] bytes = new byte[MAX_SIZE];//接收到的报文
			int len = receive(bytes);//接收	
			
			long src=Message.getSourceID(bytes);
			byte type =Message.getType(bytes);
			byte method = Message.getMethod(bytes);// 解析出method
			Date receiveTime=new Date();
			byte[] content = Message.getContent(bytes);

			// 解析content中的json
			String json = new String(content);
			JSONTokener jsonTokener = new JSONTokener(json);
			JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// 将json数据转换为jsonObject对象
			long IDC = jsonObject.getLong("IDC");
			long IDTgs = jsonObject.getLong("IDTgs");
			String timeStamp = jsonObject.getString("timeStamp");

			//判断IDTgs是否合法，否则
			if (IDTgs!=TGS) {
				Message.setRespond(bytes, DENY);
				Message.setTargetID(bytes, src);
				Message.setSourceID(bytes, AS);
				send(bytes);
				return;	
			}
			
			
			//判断是否超时
			try {
				Date timeStampDate = format.parse(timeStamp);
				long s = receiveTime.getTime() - timeStampDate.getTime();
				if (s>MAXTIME) {
					Message.setRespond(bytes, OVERTIME);
					Message.setTargetID(bytes, src);
					Message.setSourceID(bytes, AS);
					send(bytes);
					return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			//根据IDC从数据库获取C的paasswd
			String passwdOfClient=DBExcute.getPasswd((int)IDC);
			//调用MD5算法生成keyClient,用其加密后，用client的passwd解开
			String keyClient = MD5.getStringMD5(passwdOfClient);;//用keyClient对ASToClient加密
			
			//给定client与tgs的会话钥
			String string =  "ijksdahtuirwehy";
			//生成定长的keyCAndTgs
			String keyCAndTgs=MD5.getStringMD5(string);
			
			Date date=new Date();
			String timeStampInTicketTgs=format.format(date);//时间戳
			long lifeTime = MAXTIME;// 生存期
			String ADC="MACC";//C的mac地址

			// 封装ticketTgs成一个json包
			JSONObject ticketTgs = new JSONObject();
			ticketTgs.put("keyCAndTgs", keyCAndTgs);
			ticketTgs.put("IDC", IDC);
			ticketTgs.put("ADC", ADC);
			ticketTgs.put("IDTgs", IDTgs);
			ticketTgs.put("timeStamp", timeStampInTicketTgs);
			ticketTgs.put("lifeTime", lifeTime);
			
			//用tgs的公钥对ticketTgs进行非对称加密
			byte temp[]=RSA.encrypt(RSA.TGSPublicKey, ticketTgs.toString().getBytes());
			String ticketTgsEncipher = new sun.misc.BASE64Encoder().encodeBuffer(temp);

			// 封装ASToClient成一个json包
			JSONObject ASToClient = new JSONObject();
			ASToClient.put("keyCAndTgs", keyCAndTgs);
			ASToClient.put("IDTgs", IDTgs);
			ASToClient.put("timeStamp", timeStampInTicketTgs);
			ASToClient.put("lifeTime", lifeTime);
			ASToClient.put("ticketTgsEncipher", ticketTgsEncipher);
			
			
			//用clientKey对ASToClient加密
			Des.setKey(keyClient);
			byte ASToClientEncipher[]=Des.encrypt(ASToClient.toString().getBytes());

			// 将内容封装在报文中
			Message.setContent(message, ASToClientEncipher);
			//设置反馈
			Message.setRespond(message, SUCCESS);
			Message.setTargetID(message, src);
			Message.setSourceID(message, AS);
			Message.setType(message, type);
			Message.setMethod(message, method);
			
			// 发送报文
			send(message);

			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package Servers;

import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.*;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import DBManger.DBExcute;
import Databean.Information;
import Databean.User;
import Message.ContentTool;
import Message.Message;
import Security.DES.Des;
import Security.MD5.MD5;
import Security.RSA.RSA;

//这是服务器的一个线程
public class ServerThread implements Runnable{
	 private Socket socket;
	 private Logger log = Logger.getLogger("Connect-Status-Log"); //日志
	 SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	 private final long MAXTIME=100000;
	 final static int MAX_SIZE = 8216;
	 
	 final static byte ON_LINE = 1; //上线
	 final static byte ADD_FRIEND = 2; //添加好友
	 final static byte GET_CHATHISTORY = 3; //获取聊天记录
	 final static byte ALTER_PWD = 4; //修改密码
	 final static byte OFF_LINE = 5; //下线
	 final static byte CHAT = 6; //聊天
	 final static byte GET_FRIEND = 7; //获取好友列表
	 static final byte GET_OFFLINEINFO = 8; //获取离线消息
	 
	 final static byte SUCCEED = 0x01;
	 final static byte OFFLINE_MSG = 0x07;
	 final static byte FAIL = 0x09;
	 final byte OVERTIME = 6;//消息超时
	 public static Map<Long, Buffer> clients = new HashMap<Long, Buffer>();
	 
	 public ServerThread(Socket socket){
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
		// TODO Auto-generated method stub
		log.info("New connection accepted "+socket.getInetAddress()+":"+socket.getPort());
		byte[] message = new byte[MAX_SIZE];
		byte[] respond = new byte[MAX_SIZE];
		int len = -1;
		int flge = 1;
		boolean exit = false;
		
		byte[] bytes = new byte[MAX_SIZE];//接收到的报文
		len = receive(bytes);//接收	
		
		long src=Message.getSourceID(bytes);
		long tar =Message.getTargetID(bytes);
		byte type =Message.getType(bytes);
		byte method = Message.getMethod(bytes);// 解析出method
		int length = Message.getLength(message);
		Date receiveTime=new Date();	
		byte[] content = Message.getContent(bytes);
		
		// 解析content中的json
		String json = new String(content);
		JSONTokener jsonTokener = new JSONTokener(json);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// 将json数据转换为jsonObject对象
		String ticketPServerEncipher= jsonObject.getString("ticketPServerEncipher");//json
		String AuthenticatorCEncipher= jsonObject.getString("AuthenticatorCEncipher");//json
		
		byte[] bt = null;
		try {
			bt = new sun.misc.BASE64Decoder().decodeBuffer(ticketPServerEncipher);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	
		//用PServer的私钥对ticketPServerEncipher进行解密
		/**
		 * PServer服务器的私钥
		 */
        String PServerPrivateKey = "5845466237642510007808831858429739770635402089872102775762686931366401533493424043171016893994276695842910513560173645338513307126134942973524427712842130965221120966348015315684879681235574911841004408970164551310202491710336042287444504023279441234723605881926571510434233311778795125522296030085444657072037876828095896875467120182754989476950013608584079641540513587983878540270574952327491418527131111798104032001240504300223235642033561539910070046161568342861629117181045184342680877050318976969776808989138329833216817509802229071155802310865517184007959224928306420456801302809757007048251121114433360787473&24939412851792017341433983041853385544439316891084434582068824517997516912997756104244445946338318587035793654527250842689287586037725783325035506738918998572208619541146401910294900050070690254366506474231994935174581127480000859539890011078293388464297959682561142964607014423152587470955973889962228142082872111530342973041750666450651149645148894221551259287587540100801244160277084159093706446139061973799178505817972182958748332709022690079998427857362092625678433711746599108438156823702493991805026529354100045300968836980819234474531351499937273165363552991339465793345974870738216141754799426493447310154867";
        byte temp[]=RSA.decrypt(PServerPrivateKey, bt);
        String ticketPServer=new String(temp);    
		
		//将ticketPServer解析出来
		JSONTokener jsonTokener2 = new JSONTokener(ticketPServer);
		JSONObject jsonticketPServer = (JSONObject) jsonTokener2.nextValue();// 将json数据转换为jsonObject对象
//		String keyCAndPServer=jsonticketPServer.getString("keyCAndPServer");
		long IDC = jsonticketPServer.getLong("IDC");
		String timeStampInticketPServer=jsonticketPServer.getString("timeStamp");
		long lifeTime=jsonticketPServer.getLong("lifeTime");
		
		//ticketPServer比较时间戳和生存期
		try {
			Date timeStampInTicketTgsDate = format.parse(timeStampInticketPServer);
			long s = receiveTime.getTime() - timeStampInTicketTgsDate.getTime();
			if (s>lifeTime) {
				Message.setRespond(bytes, OVERTIME);
				Message.setTargetID(bytes, src);
				Message.setSourceID(bytes, tar);
				send(bytes);
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//生成client与PServer的会话钥
		String str =  "asdhfafsfjhfaksfkooiij";
		//生成定长的keyCAndTgs
		String keyCAndPServer = MD5.getStringMD5(str);
		
		byte[] bt1 = null;
		try {
			bt1 = new sun.misc.BASE64Decoder().decodeBuffer(AuthenticatorCEncipher);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//用client与PServer的会话钥给AuthenticatorCEncipher解密
		try {
			Des.setKey(keyCAndPServer);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		byte[] temp3 = null;
		try {
			temp3 = Des.decrypt(bt1);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String AuthenticatorC = new String(temp3);
		
		//将AuthenticatorC解析出来
		JSONTokener jsonTokener3 = new JSONTokener(AuthenticatorC);
		JSONObject jsonAuthenticatorC = (JSONObject) jsonTokener3.nextValue();// 将json数据转换为jsonObject对象
		String timeStampInAuthenticatorC=jsonAuthenticatorC.getString("timeStamp");
		
		String timeAdd=timeStampInAuthenticatorC+1;
		
		// 封装PServerToClient成一个json包
		JSONObject PServerToClient = new JSONObject();
		PServerToClient.put("timeStamp", timeAdd);
		
		byte[] PServerToClientEncipher = null;
		//用client与PServer的会话钥对PServerToClient加密
		try {
			Des.setKey(keyCAndPServer);
			PServerToClientEncipher=Des.encrypt(PServerToClient.toString().getBytes());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// 将内容封装在报文中
		Message.setContent(message, PServerToClientEncipher);
		//设置反馈
		Message.setRespond(message, SUCCEED);
		Message.setTargetID(message, src);
		Message.setSourceID(message, tar);
		Message.setType(message, type);
		Message.setMethod(message, method);

		// 发送报文
		send(message);
		while((len = receive(message)) != -1){
			try {
				log.info(""+message.length);
				message = Des.decrypt(message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int mathod = Message.getMethod(message);
			tar = Message.getTargetID(message);
			src = Message.getSourceID(message);
			type = Message.getType(message);
			if(type != 1)
				continue;
			
			switch(mathod){
			case GET_FRIEND:{
				List<User> users = DBExcute.viewFriends((int)src);
				respond = Message.getRespondMessage(src, tar, (byte)type, SUCCEED, ContentTool.getUserBytes(users));
				try {
					respond = Des.encrypt(respond);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(respond);
				break;
			}
			case ON_LINE: {
				if(!clients.containsKey(src)){
					clients.put(src, new Buffer());
					DBExcute.logIn((int)src);
				}
				if(flge == 1){ //创建一个转发线程
					new Forward(clients.get(src),socket).start();
					flge = 0;
				}
				String name = DBExcute.logIn((int)src);
				if(name != null){
					respond = Message.getRespondMessage(src, tar, (byte)Information.TYPE, SUCCEED, name.getBytes());
				}else
					respond = Message.getRespondMessage(src, tar, (byte)Information.TYPE, FAIL,null);
				try {
					respond = Des.encrypt(respond);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(respond);
				log.info("user "+ src +" is oline!");
				break;
			}
			case ADD_FRIEND:{
				Information info = ContentTool.getInfo(Message.getContent(message));
				if(DBExcute.addFriend((int)src, (int)info.getTo())==1){
					send(Message.getRespondMessage(src, tar, (byte)type, SUCCEED, null));
				}else{
					send(Message.getRespondMessage(src, tar, (byte)type, FAIL, null));
				}
				break;
			}
			
			case GET_CHATHISTORY:{
				List<Information> infos = DBExcute.checkChatHistory((int)src, (int)ContentTool.getInfo(Message.getContent(message)).getTo());
				send(Message.getRespondMessage(src, tar, (byte)Information.TYPE, SUCCEED, ContentTool.getInfoBytes(infos)));
				break;
			}
			case ALTER_PWD:{
				String pwd = ContentTool.getInfor(Message.getContent(message));
				if(DBExcute.changePasswd((int)src, pwd)==1){
					send(Message.getRespondMessage(src, tar, (byte)type, SUCCEED, null));
				}else{
					send(Message.getRespondMessage(src, tar, (byte)type, FAIL, null));
				}
				break;
			}
			case OFF_LINE:{
				clients.remove(src);
				DBExcute.offline((int) src);
				log.info("usre "+src+"offline!");
				exit = true;
				break;
			}
			case CHAT:{
				if(clients.containsKey(tar)){ //在线
					log.info("get info: "+ new String(message));
					DBExcute.recordInformation(new Information(ServerForMultiClient.infoID++, new String(Message.getContent(message)), src, tar),1);
					try {
						message = Des.encrypt(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					clients.get(tar).put(message);
					
					send(Message.getRespondMessage(src, tar, (byte)type, (byte)SUCCEED, null));
				}else{//离线
					DBExcute.recordInformation(new Information(ServerForMultiClient.infoID++, new String(Message.getContent(message)), src, tar),0);
					send(Message.getRespondMessage(src, tar, (byte)type, (byte)OFFLINE_MSG, null));
				}
				break;
			}
			}
			if(exit)
				break;
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

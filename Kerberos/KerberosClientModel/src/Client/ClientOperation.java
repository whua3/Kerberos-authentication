package Client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import Message.Message;
import Security.DES.Des;
import Security.MD5.MD5;
import Security.RSA.RSA;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

public class ClientOperation {
	static byte[] sendBuffer = new byte[8216];
	static byte[] receiveBuffer = new byte[8216];
	static DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static long MAXTIME=100000; 
	
	// 源
	private final static long AS = 1;// AS服务器
	private final static long TGS = 2;// TGS服务器
	private final static long PSERVERCHAT = 3;// 应用服务器(消息)
	private final static long PSERVERFILE = 4;// 应用服务器(文件)
	
	 //反馈respond
	 private final static byte NEGATIVE = 0;//该字段未被激活
	 private final static byte SUCCESS = 1;//操作成功
	 private final static byte DENY = 2;//拒绝服务
	 private final static byte FAILTOUPLOAD = 3;//文件上传失败
	 private final static byte FAILTODOWNLOAD = 4;//文件下载失败
	 private final static byte USEROFFLINE = 5;//消息投递成功，但用户处于离线状态
	 private final static byte OVERTIME = 6;//消息超时
	 
	 private final static byte CONNSUCCESS = 7;//验证成功
	 private final static byte CONNFAIL = 8;//验证失败
	 
	 public static String keyCT;//client和Tgs的会话钥
	
	//client封装报文，并发送给AS
	public static byte[] clientToAS(long IDC,long IDTgs){

		JSONObject clientToAs = new JSONObject();
		clientToAs.put("IDC", IDC);
		clientToAs.put("IDTgs", IDTgs);
		Date time =new Date();
		String timeString=format.format(time);
		clientToAs.put("timeStamp", timeString);
		
		Message.setContent(sendBuffer, clientToAs.toString().getBytes());
		Message.setTargetID(sendBuffer, AS);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, (byte)0);
		Message.setMethod(sendBuffer, (byte)0);
		Message.setRespond(sendBuffer, (byte)0);
		
		ConnManger cm = new ConnManger("as");
		SocketConn conn = cm.getConn();
		conn.send(sendBuffer);
		conn.receive(receiveBuffer);
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receiveBuffer;
	}
	
	//client将从AS收到的报文进行解析,若成功则向Tgs发送报文
	public static byte[] clientToTgs(long IDC, String pwd, long IDPServer,byte receiveBuffer[]) throws IOException, Exception{
		//从数据库中获取IDC的passwd，并用MD5得到解密钥匙
		String passwdOfClient= pwd;
		//调用MD5算法生成keyClient,用其加密后，用client的passwd对应的MD5解开
		String keyClient = MD5.getStringMD5(passwdOfClient);;//用keyClient对ASToClient加密
		
		ConnManger cm = new ConnManger("tgs");
		SocketConn conn = cm.getConn();
		Date receiveTime=new Date();
		byte respond =Message.getRespond(receiveBuffer);
		byte type =Message.getType(receiveBuffer);
		byte method = Message.getMethod(receiveBuffer);// 解析出method
		
		//判断respond的回应
		if (respond!=(byte)1) {
			byte[] erro={(byte)respond};
			return erro;
		}
		
		byte[] contentEncipher = Message.getContent(receiveBuffer);
		//用keyClient对content解密
		Des.setKey(keyClient);
		byte[] content=Des.decrypt(contentEncipher);
		
		// 解析content中的json
		String json = new String(content);
		JSONTokener jsonTokener = new JSONTokener(json);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// 将json数据转换为jsonObject对象
		String keyCAndTgs = jsonObject.getString("keyCAndTgs");
		keyCT=keyCAndTgs;
		long IDTgs = jsonObject.getLong("IDTgs");
		String timeStamp = jsonObject.getString("timeStamp");
		long lifeTime = jsonObject.getLong("lifeTime");
		String ticketTgsEncipher = jsonObject.getString("ticketTgsEncipher");
		
		//判断是否超时
		try {
			Date timeStampDate = format.parse(timeStamp);
			long s = receiveTime.getTime() - timeStampDate.getTime();
			if (s>lifeTime) {
				Message.setRespond(sendBuffer, OVERTIME);
				Message.setTargetID(sendBuffer, IDC);
				Message.setSourceID(sendBuffer, AS);
				conn.send(sendBuffer);
				byte[] erro={(byte)OVERTIME};
				return erro;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//将AuthenticatorC封装成json
		JSONObject AuthenticatorC=new JSONObject();
		AuthenticatorC.put("IDC", IDC);
		String ADC="MACC";//C的mac地址
		AuthenticatorC.put("ADC", ADC);
		Date sendTime=new Date();
		String sendTimeString=format.format(sendTime);//时间戳
		AuthenticatorC.put("timeStamp", sendTimeString);

		//用keyCAndTgs对AuthenticatorC加密（DES），得到String类型AuthenticatorEncipher
		Des.setKey(keyCAndTgs);
		byte[] temp = Des.encrypt(AuthenticatorC.toString().getBytes());
		String AuthenticatorEncipher = new sun.misc.BASE64Encoder().encodeBuffer(temp);
		
		//将Client发送给tgs的content封装成json
		JSONObject ClientToTgs = new JSONObject();
		ClientToTgs.put("IDPServer", IDPServer);
		ClientToTgs.put("ticketTgsEncipher", ticketTgsEncipher);
		ClientToTgs.put("AuthenticatorEncipher", AuthenticatorEncipher);
		
		// 将内容封装在报文中
		Message.setContent(sendBuffer, ClientToTgs.toString().getBytes());
		//设置反馈
		Message.setRespond(sendBuffer, SUCCESS);
		Message.setTargetID(sendBuffer, TGS);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, type);
		Message.setMethod(sendBuffer, method);
		
		conn.send(sendBuffer);
		conn.receive(receiveBuffer);
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receiveBuffer;	
	}
	
	//client将从Tgs收到的报文进行解析,若成功则向PServer发送报文
	public static byte clientToPSever(long IDC,byte receiveBuffer[]) throws Exception{		
		Date receiveTime=new Date();
		byte respond =Message.getRespond(receiveBuffer);
		byte type =Message.getType(receiveBuffer);
		byte method = Message.getMethod(receiveBuffer);// 解析出method
		
		//判断respond的回应
		if (respond!=(byte)1) {
			return respond;
		}
		
		byte[] temp = Message.getContent(receiveBuffer);
		//用client和Tgs的会话钥对content解密
		Des.setKey(keyCT);
		byte[]temp2= Des.decrypt(temp);
		String content=new String(temp2);
		
		// 解析content中的json
		String json = new String(content);
		JSONTokener jsonTokener = new JSONTokener(json);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// 将json数据转换为jsonObject对象
//		String keyCAndPServer = jsonObject.getString("keyCAndPServer");
		long IDPServer = jsonObject.getLong("IDPServer");
		String timeStamp = jsonObject.getString("timeStamp");
		String ticketPServerEncipher = jsonObject.getString("ticketPServerEncipher");
		
		SocketConn conn;
		if (IDPServer == PSERVERCHAT) {
			ConnManger cm = new ConnManger("CHATSERVER");
			conn = cm.getConn();
		} else {
			ConnManger cm = new ConnManger("FILESERVER");
			conn = cm.getConn();
		}
		
		//判断是否超时
		try {
			Date timeStampDate = format.parse(timeStamp);
			long s = receiveTime.getTime() - timeStampDate.getTime();
			if (s>MAXTIME) {
				Message.setRespond(sendBuffer, OVERTIME);
				Message.setTargetID(sendBuffer, TGS);
				Message.setSourceID(sendBuffer, IDC);
				conn.send(sendBuffer);
				return OVERTIME;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//将AuthenticatorC封装成json
		JSONObject AuthenticatorC=new JSONObject();
		AuthenticatorC.put("IDC", IDC);
		String ADC="MACC";//C的mac地址
		AuthenticatorC.put("ADC", ADC);
		Date sendTime=new Date();
		AuthenticatorC.put("timeStamp", sendTime);
		
		//生成client与PServer的会话钥
		String str =  "asdhfafsfjhfaksfkooiij";
		//生成定长的keyCAndTgs
		String keyCAndPServer=MD5.getStringMD5(str);
		
		//用client和PServer的会话钥对AuthenticatorC进行加密
		Des.setKey(keyCAndPServer);
		byte[] temp1=Des.encrypt(AuthenticatorC.toString().getBytes());
		String AuthenticatorCEncipher = new sun.misc.BASE64Encoder().encodeBuffer(temp1);
		
		//将Client发送给PServer的content封装成json
		JSONObject ClientToPServer = new JSONObject();
		ClientToPServer.put("ticketPServerEncipher", ticketPServerEncipher);
		ClientToPServer.put("AuthenticatorCEncipher", AuthenticatorCEncipher);
		
		// 将内容封装在报文中
		Message.setContent(sendBuffer, ClientToPServer.toString().getBytes());
		//设置反馈
		Message.setRespond(sendBuffer, SUCCESS);
		Message.setTargetID(sendBuffer, IDPServer);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, type);
		Message.setMethod(sendBuffer, method);
		
		conn.send(sendBuffer);
		
		conn.receive(receiveBuffer);
		
		
		byte[] contentPSEncipher = Message.getContent(receiveBuffer);
		//用client和PServer的会话钥对contentPS解密
		Des.setKey(keyCAndPServer);
		byte[] contentPS=Des.decrypt(contentPSEncipher);
		// 解析content中的json
		String json1 = new String(contentPS);
		JSONTokener jsonTokener1 = new JSONTokener(json1);
		JSONObject jsonObject2 = (JSONObject) jsonTokener1.nextValue();// 将json数据转换为jsonObject对象
		String timeStampFromPS = jsonObject2.getString("timeStamp");

		if (timeStampFromPS.equals(sendTime.toString()+1)) {
			return CONNFAIL;
		}
		return CONNSUCCESS;
	}
}

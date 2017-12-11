package Message;

import java.util.logging.Logger;

/**
 * 数据报文的封装与解析
 * @author gaoshao
 *
 */
public class Message {
	
	private final static int MAX_SIZE = 8216;
	
	private static Logger log = Logger.getLogger("Message-Log");
	
	/**
	 * 获取目标的id
	 * @param message
	 * @return
	 */
	public static long getTargetID(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1l;
		}
		long result = 0x0000000000000000l;
		for(int i=0; i<8; i++){
			long c = (char)(message[i] & 0x00ff);
			result = result | (c << (8*i));
		}
		return result;
	}
	
	/**
	 * 设置报文的Tar字段
	 * @param message
	 * @param value
	 */
	public static void setTargetID(byte[] message, long value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return;
		}
		for(int i=0; i<8; i++){
			message[i] = (byte) (value >> (8*i));
		}
	}
	
	/**
	 * 获取源id
	 * @param message
	 * @return
	 */
	public static long getSourceID(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1l;
		}
		long result = 0x0000000000000000l;
		for(int i=0; i<8; i++){
			long c = (char)(message[i+8] & 0x00ff);
			result = result | (c << (8*i));
		}
		return result;
	}
	
	/**
	 * 设置报文src字段的值
	 * @param message
	 * @param value
	 */
	public static void setSourceID(byte[] message, long value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return;
		}
		for(int i=0; i<8; i++){
			message[i+8] = (byte) (value >> (8*i));
		}
	}
	
	/**
	 * 获取报文type字段
	 * @param message
	 * @return 
	 */
	public static byte getType(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1;
		}
		return message[16];
	}
	
	/**
	 * 设置报文type字段
	 * @param message
	 * @param value
	 */
	public static void setType(byte[] message, byte value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return ;
		}
		message[16] = value;
	}
	
	/**
	 * 获取报文Method字段
	 * @param message
	 * @return 
	 */
	public static byte getMethod(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1;
		}
		return message[17];
	}
	
	/**
	 * 设置报文Method字段
	 * @param message
	 * @param value
	 */
	public static void setMethod(byte[] message, byte value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return ;
		}
		message[17] = value;
	}
	
	/**
	 * 获取报文Respond字段
	 * @param message
	 * @return 
	 */
	public static byte getRespond(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1;
		}
		return message[18];
	}
	
	/**
	 * 设置报文Respond字段
	 * @param message
	 * @param value
	 */
	public static void setRespond(byte[] message, byte value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return ;
		}
		message[18] = value;
	}
	
	/**
	 * 获取报文length字段
	 * @param message
	 * @return
	 */
	public static int getLength(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return -1;
		}
		int length = 0;
		for(int i=0; i<2; i++){
			int c = (char)(message[i+19] & 0x00ff);
			length = length | (c << (8*i));
		}
		return length;
	}
	
	/**
	 * 设置报文length字段
	 * @param message
	 * @param value
	 */
	public static void setLength(byte[] message, int value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return ;
		}
		for(int i=0; i<2; i++){
			message[i+19] = (byte) (value >> (8*i));
		}
	}
	
	/**
	 * 获取报文的Content内容，
	 * @param message
	 * @return Content的字节数组
	 */
	public static byte[] getContent(byte[] message){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return null;
		}
		int length = getLength(message);
		byte[] content = new byte[length];
		int i;
		for(i=0; i < length; i++){
			content[i] = message[i+24];
		}
		return content;
	}
	
	/**
	 * 设置报文的Content内容
	 * @param message
	 * @param value
	 */
	public static void setContent(byte[] message, byte[] value){
		if(message.length < 24){
			log.warning("Illegal message by the length no more than 24 bytes.");
			return;
		}
		if(value.length == 0){
			log.warning("The vlaue is null.");
			return;
		}
		if(value.length + 24 >= MAX_SIZE){
			log.warning("The length of value is illegal.");
			return;
		}
		int length = value.length;
		setLength(message, length);
		int i;
		for(i=24;i < length+24 && i < MAX_SIZE ; i++ ){
			message[i] = value[i-24];
		}
	}
	
//	public static void main(String[] argv){
//		byte[] message = new byte[8216];
//		Message.setContent(message, "hello world!".getBytes());
//		byte[] bytes = Message.getContent(message);
//		for(byte b : bytes){
//			System.out.print((char)b);
//		}
//	}
}

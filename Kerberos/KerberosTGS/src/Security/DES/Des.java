package Security.DES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import Security.MD5.MD5;

/**
 * 
 * @author gaoshao
 *
 */
public class Des {
	
	private static KeyManger keyManger = null;
	
	/**
	 * 根据S盒获取32位数据块
	 * @param expand
	 * @return
	 */
	private static BitManger checkTableS(BitManger expand){
		BitManger result = new BitManger(32);
		int i,j;
		
		for(i = 0; i < 8; i++){
			byte r=0x00,c=0x00;
			for(j = 0; j < 6; j++){
				if(expand.get(j+i*6)){
					switch(j){
					case 0: r |= 0x02; break;
					case 1: c |= 0x08; break;
					case 2: c |= 0x04; break;
					case 3: c |= 0x02; break;
					case 4: c |= 0x01; break;
					case 5: r |= 0x01; break;
					}
				}
			}
			
			byte b = Table.S[i][r][c];
			for(j = 0; j < 4; j++){
				if((b&(0x01 << j))!= 0x00){
					result.set(3-j+i*4,true);
				}
			}
		}
		return result;
	}
	
	/**
	 * 混乱，扩散函数
	 * @param right
	 * @param key
	 * @return
	 */
	private static BitManger Fun(BitManger right, BitManger key){
		BitManger result = new BitManger(32);
		BitManger expand = new BitManger(48);
		int i;
		for(i = 0; i < 48; i++)
			expand.set(i,right.get(Table.E[i]-1));
		expand.xor(key);
		BitManger temp = checkTableS(expand);
		for(i = 0; i < 32; i++)
			result.set(i,temp.get(Table.P[i]-1));
		return result;
	}
	
	
	
	/**
	 * 设置密码
	 * @param key 
	 * @throws Exception 
	 */
	public static void setKey(String key) throws Exception{
		key = MD5.getStringMD5(key); //转化为长度为8的字符串
		if(keyManger == null)
			keyManger = KeyManger.getKeyManger();
		keyManger.setSrcKey(key);
	}
	
	/**
	 * 加密一个byte数组
	 * @param content
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static byte[] encrypt(byte[] src) throws IOException, Exception{
		
		byte[] buffer = new byte[8];
		
		BitManger content = null;
	    keyManger = KeyManger.getKeyManger();
	    
	    ByteArrayInputStream bytesIn = new ByteArrayInputStream(src);
	    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	    
	    int len = -1;
	    while((len = bytesIn.read(buffer)) != -1){
	    	if(len < 8){
	    		for(int k=len; k<8; k++)
	    			buffer[k] = 0x20;
	    	}
	    	content = new BitManger(buffer);
	    	content.contentIP();
	    	
	    	for(int count = 0; count < 16 ; count++){
	    		content.xor(Fun(content.getRight(),keyManger.getKey(count)));
	    		if(count < 15)
	    			content.switchLR();
	    	}
	    	content.contentIPR();
	    	buffer = content.getBytes();
	    	bytesOut.write(buffer, 0, 8);
	    }
	    bytesIn.close();
	    bytesOut.close();
		return bytesOut.toByteArray();
	}
	
	public static byte[] decrypt(byte[] src) throws IOException, Exception{
		byte[] buffer = new byte[8];
		
		BitManger content = null;
	    keyManger = KeyManger.getKeyManger();
	    
	    ByteArrayInputStream bytesIn = new ByteArrayInputStream(src);
	    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	    
	    while((bytesIn.read(buffer)) != -1){
	    	content = new BitManger(buffer);
	    	content.contentIP();
	    	
	    	for(int count = 15; count >= 0 ; count--){
	    		content.xor(Fun(content.getRight(),keyManger.getKey(count)));
	    		if(count > 0)
	    			content.switchLR();
	    	}
	    	content.contentIPR();
	    	buffer = content.getBytes();
	    	bytesOut.write(buffer, 0, 8);
	    }
	    bytesIn.close();
	    bytesOut.close();
	    return bytesOut.toByteArray();
	}
}

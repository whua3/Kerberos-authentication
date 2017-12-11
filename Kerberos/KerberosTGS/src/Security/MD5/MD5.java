package Security.MD5;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5的实现
 * @author gaoshao
 *
 */
public class MD5 {
	
	/**
	 * 返回一个字符串的MD5值
	 * @param input
	 * @return
	 */
	public static String getStringMD5(String input){
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			
			byte[] inputByteArray = input.getBytes();
			
			md5.update(inputByteArray);
			byte[] resultByteArray = md5.digest();
			
			return byteArrayToString(resultByteArray);
		}catch(NoSuchAlgorithmException e){
			return null;
		}
	}
	
	/**
	 * 返回一个文件的MD5值
	 * @param fileInputStream
	 * @return
	 * @throws IOException 
	 */
	public static String getFileMD5(FileInputStream fileInputStream) throws IOException{
		int bufferSize = 256 * 1024;
		DigestInputStream digestInputStream = null;
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			
			digestInputStream = new DigestInputStream(fileInputStream,md5);
			
			byte[] buffer =new byte[bufferSize];  
			while (digestInputStream.read(buffer) > 0);
			
			md5 = digestInputStream.getMessageDigest();
			byte[] resultByteArray = md5.digest();
			
			return byteArrayToString(resultByteArray);
			
		}catch(NoSuchAlgorithmException e){
			return null;
		}finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {}
			try {
				fileInputStream.close();
			} catch (Exception e) {}
		}  
	}
	
	/**
	 * 将一个byte数组装换成16进制表示的字符串
	 * @param byteArray
	 * @return
	 */
	private static String byteArrayToHexString(byte[] byteArray){
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
		
		char[] resultCharArray =new char[byteArray.length * 2];
		
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b& 0xf];
		}
		return new String(resultCharArray); 
	}
	
	/**
	 * 将一个byte数组装换成长度为8的字符串
	 * @param byteArray
	 * @return
	 */
	private static String byteArrayToString(byte[] byteArray){
		byte[] chars = new byte[8];
		int i;
		for (i=0; i < 8; i++) {
			byte l = byteArray[2*i];
			byte h =  byteArray[2*i+1];
			chars[i] = (byte)(h&l);
		}
		return new String(chars);
	}
}

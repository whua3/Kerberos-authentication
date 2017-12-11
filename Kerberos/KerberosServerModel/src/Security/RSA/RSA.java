package Security.RSA;
import java.math.BigInteger;

public class RSA {
	
	/**
	 * RSA加密一组byte
	 * @param Key 加密数据时用公钥，做签名时用私钥
	 * @param src 
	 * @return 
	 */
	public static byte[] encrypt(String Key, byte[] src){
		
		BigInteger a = new BigInteger(KeyManger.getKeyMain(Key));
		BigInteger n = new BigInteger(KeyManger.getKeyN(Key));
		
		BigInteger m = new BigInteger(src);
		BigInteger c = m.modPow(a, n);
		
        return c.toByteArray();
	}
	
	/**
	 * RSA解密一组byte
	 * @param Key 解密数据时用私钥，认证签名时用公钥
	 * @param src 
	 * @return 
	 */
	public static byte[] decrypt(String Key, byte[] src){
		
		StringBuffer buffer = new StringBuffer();
		BigInteger a = new BigInteger(KeyManger.getKeyMain(Key));
		BigInteger n = new BigInteger(KeyManger.getKeyN(Key));
		
		BigInteger c = new BigInteger(src);
		BigInteger m = c.modPow(a, n);
		
		byte[] tarBytes = m.toByteArray();
		
		for(int i=0;i<tarBytes.length;i++){
			buffer.append((char)tarBytes[i]);
        }
        return buffer.toString().getBytes();
	}
	
	public static void main(String[] args){
		for(int i=0; i< 2; i++){
		KeyManger keyManger = new KeyManger();
		String publicKey = keyManger.getPublicKey();
		String privateKey = keyManger.getPrivateKey();
		System.out.println(publicKey+"\n"+privateKey);
		
		}
		
	}
}

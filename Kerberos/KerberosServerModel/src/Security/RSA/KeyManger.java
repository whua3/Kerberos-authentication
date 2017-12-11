package Security.RSA;
import java.math.BigInteger;
import java.security.SecureRandom;


public class KeyManger {
	
	private BigInteger e;
	
	private BigInteger n;
	
	private BigInteger d;
	
	/**
	 * 默认的构造函数，尝试生成2048位的密钥
	 */
	public KeyManger(){
		this(1024);
	}
	
	/**
	 * 带参数的构造函数，生成2*nbit位的密钥
	 * @param nbit 参数p和q的位数
	 */
	public KeyManger(int nbit){
		BigInteger p = new BigInteger(nbit,99,new SecureRandom());
		BigInteger q = new BigInteger(nbit,99,new SecureRandom());
		n = q.multiply(p);
		BigInteger fai = q.subtract(new BigInteger("1")).multiply(p.subtract(new BigInteger("1")));
		e = new BigInteger("65537");
		d = e.modInverse(fai);
	}
	
	/**
	 * 将参数e和n拼接成公钥返回
	 * @return 公钥字符串
	 */
	public String getPublicKey(){
		return e.toString()+"&"+n.toString();
	}
	
	/**
	 * 将参数d和n拼接成私钥返回
	 * @return 私钥字符串
	 */
	public String getPrivateKey(){
		return d.toString()+"&"+n.toString();
	}
	
	/**
	 * 获取密钥串中的主参数，e或d
	 * @param keyString 公钥或私钥字符串
	 * @return 返回主参数
	 */
	public static String getKeyMain(String keyString){
		String[] result = keyString.split("&");
		if(result.length != 2)
			return null;
		else
			return result[0];
	}
	
	/**
	 * @param keyString 公钥或私钥字符串
	 * @return 返回N
	 */
	public static String getKeyN(String keyString){
		String[] result = keyString.split("&");
		if(result.length != 2)
			return null;
		else
			return result[1];
	}
}

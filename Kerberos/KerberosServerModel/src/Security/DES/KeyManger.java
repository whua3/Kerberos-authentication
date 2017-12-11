package Security.DES;

/**
 * 从64位输入秘钥中提取出56为，生成16组48位加密钥
 * @author gaoshao
 *
 */
public class KeyManger {
	
	private static KeyManger instance = null;
	
	private String srcKey = "GAoShAO$";
	
	private BitManger[] keys = new BitManger[16];
	
	/**
	 * 使用默认的原始密钥生成keys
	 * @throws Exception 
	 */
	private KeyManger() throws Exception{
		initKeys();
	}
	
	/**
	 * 初始化keys
	 * @throws Exception 
	 */
	private void initKeys() throws Exception{
		byte[] bytes = this.srcKey.getBytes();
		BitManger temp = new BitManger(bytes);
		BitManger srcKeyBit = new BitManger(56);
		int i;
		for(i=0; i < 56; i++){
			srcKeyBit.set(i, temp.get(Table.PC1[i]-1));
		}
		for(i=0; i < 16; i++){
			keys[i] = new BitManger(48);
			srcKeyBit.rotateLeft(0, 28, Table.Loop[i]);
			srcKeyBit.rotateLeft(28, 56, Table.Loop[i]);
			for(int j=0; j < 48; j++)
				keys[i].set(j,srcKeyBit.get(Table.PC2[j]-1));
		}
	}
	
	/**
	 * 返回KeyManger的实例
	 * @return
	 */
	public static KeyManger getKeyManger(){
		if(instance == null){
			try {
				instance = new KeyManger();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	/**
	 * 修改默认的原始秘钥
	 * @param srcKey 
	 * @throws Exception 
	 */
	public void setSrcKey(String srcKey) throws Exception{
		this.srcKey = srcKey;
		initKeys();
	}
	
	/**
	 * 
	 * @param index 子钥匙号
	 * @return 返回编号为index的子钥匙
	 */
	public BitManger getKey(int index){
		return keys[index];
	}
}

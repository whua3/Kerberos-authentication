package Security.DES;
import java.util.BitSet;


public class BitManger extends BitSet {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] bytes = null;
	
	public BitManger(int nbits){
		super(nbits);
		bytes = new byte[nbits/8];
	}
	
	/**
	 * 初始化一个bit管理器
	 * @param bytes 用数组大小不超过8的byte数组来初始化一个64位的bit管理器
	 * @throws Exception param should no more than 64bit
	 */
	public BitManger(byte[] bytes) throws Exception{
		super(64);
		this.bytes = bytes;
		if(this.bytes.length > 8){
			throw new Exception("param should no more than 64bit");
		}
		for(int i=0; i < this.bytes.length; i++){
			for(int j=0; j < 8; j++){
				if((this.bytes[i]&(0x01 << j))!= 0x00)
					set(i*8+j);
			}
		}
	}

	/*
	 * 用IP表将原始明文打乱
	 */
	public void contentIP(){
		BitManger temp = new BitManger(64);
		int i;
		for(i=0; i<64; i++){
			temp.set(i, get(i));
		}
		
		for(i=0; i<64; i++){
			set(i,temp.get(Table.IP[i]-1));
		}
		
		temp = null;
	}
	
	/*
	 * 用IPR表恢复明文
	 */
	public void contentIPR(){
		BitManger temp = new BitManger(64);
		int i;
		for(i=0; i<64; i++){
			temp.set(i, get(i));
		}
		
		for(i=0; i<64; i++){
			set(i,temp.get(Table.IPR[i]-1));
		}
		
		temp = null;
	}
	/**
	 *
	 * @return 经过一系列加密或解密操作后的byte数组
	 */
	public byte[] getBytes(){
		byte[] result = new byte[this.bytes.length];
		for(int i=0; i < this.bytes.length; i++){
			for(int j=0; j < 8; j++){
				if(get(i*8+j)){
					result[i] |= 0x01 << j;
				}
			}
		}
		return result;
	}
	
	/**
	 * 返回一个数据块的右部
	 * @return
	 */
	public BitManger getRight(){
		BitManger right = new BitManger(32);
		int i;
		for(i=0;i<32;i++)
			right.set(i,get(i+32));
		return right;
	}
	
	/**
	 * 从起点到终点循环n位
	 * @param start 起点
	 * @param end 终点
	 * @param n 
	 * @throws Exception 
	 */
	public void rotateLeft(int start, int end, int n) throws Exception{
		if(start < 0 || end < 0 || start > 64 || end > 64 || start > end){
			throw new Exception("illegal index of statr or end in rotateLeft(int start, int end, int n)");
		}
		int i;
		boolean[] temp = new boolean[n];
		for(i=0; i<n; i++){
			temp[i] = get(i+start);
		}
		for(i = start ; i < end-n ; i++){
			set(i,get(i+n));
		}
		for(i = 0; i < n; i++){
			set(i + end -n,temp[i]);
		}
	}
	
	/**
	 * 左右交换
	 */
	public void switchLR(){
		try {
			rotateLeft(0,64,32);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		for(int i=0; i < 64; i++){
			if(get(i))
				b.append('1');
			else
				b.append('0');
		}
		return b.toString();
	}
}

package Servers;
/**
 * 线程的消息缓存，用于多线程之间消息的交换
 * @author gaoshao
 *
 */
public class Buffer {
	private byte[] message = new byte[8216];
	private boolean available = false;
	
	/**
	 * 读消息
	 * @return
	 */
	public synchronized byte[] get(){
		while(available == false){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		available = false;
		notify();
		return message;
	}
	
	/**
	 * 将消息写进缓存
	 * @param value
	 */
	public synchronized void put(byte[] value){
		while(available){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int i=0;
		for(byte b : value){
			message[i++] = b;
		}
		
		available = true;
		notify();
	}
}

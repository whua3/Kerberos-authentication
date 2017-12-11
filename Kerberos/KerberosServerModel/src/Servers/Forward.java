package Servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class Forward extends Thread {
	private Buffer buffer;
	private Socket socket;
	private Logger log = Logger.getLogger("Connect-Status-Log"); //»’÷æ
	
	public Forward(Buffer buffer, Socket socket){
		this.buffer = buffer;
		this.socket = socket;
	}
	
	private void send(byte[] content){
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
	
	public void run(){
		while(true){
			byte[] message = buffer.get();
			send(message);
		}
	}
}

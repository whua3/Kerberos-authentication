package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public abstract class SocketConn {
	
	final static int port = 8888;
	
	final static int MAX_SIZE = 8216;
	
	static Socket socket = null;
	
	static SocketConn conn = null;
	
	static Logger log = Logger.getLogger("Connect-Status-Log"); 
	
	
	public void send(byte[] content){
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
	
	public int receive(byte[] result){
		int len = -1;
		try {
			InputStream socketIn = socket.getInputStream();
			len = socketIn.read(result, 0, MAX_SIZE);
			log.info("Messeag has been received!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warning("Fail to receive Messeag due to IOException!");
			e.printStackTrace();
		}
		return len;
	}
	
	public void close() throws IOException{
		socket.close();
		socket = null;
	}
}

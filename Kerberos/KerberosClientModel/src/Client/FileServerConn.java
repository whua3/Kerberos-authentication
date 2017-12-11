package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileServerConn extends SocketConn {
	private static Socket fileConn = null;
	
	private static FileServerConn conn = null;
	
	private FileServerConn(){
		socket = null;
		try {
			fileConn = new Socket("localhost", port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static FileServerConn getConn(){
		if(conn == null)
			conn = new FileServerConn();
		return conn;
	}
	
	public void send(byte[] content){
		try {
			OutputStream socketOut = fileConn.getOutputStream();
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
			InputStream socketIn = fileConn.getInputStream();
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
		fileConn.close();
		fileConn = null;
	}
}

package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TGSConn extends SocketConn{
	public TGSConn(){
		try {
			socket = new Socket("192.168.0.1", port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package wgl;

import APP.Application;
import Client.ConnManger;
import Client.SocketConn;
import Message.Message;
import Security.DES.Des;

public class BackgroundThread extends Thread {
	
	SocketConn conn = null;
	byte[] message = new byte[8216];
	byte[] buffer = new byte[8216];
	
	public BackgroundThread(){
		super();
		if(Application.cm == null)
			Application.cm = new ConnManger("chatserver");
		conn = Application.cm.getConn();
	}
	
	public void run(){//
		byte[] bytes = new byte[8216];
		
		while(conn.receive(bytes)!=-1){
			
			System.out.println(new String(bytes));
			try {
				bytes = Des.decrypt(bytes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long src = Message.getSourceID(bytes);
			String s = new String(Message.getContent(bytes));
			
			try {
				GpChat c = Chat.usrSet.get(src);
				c.displayInfo(s);
				Chat.usrSet.get(src).setVisible(true);
			} catch (Exception e) {
				
			}
 			
		}
	}
}
